import { describe, expect, it, vi } from 'vitest';
import { ApiClient } from './client';
import { ApiError, responseError } from './errors';

function jwt(revision = 0): string {
  return 'test.' + btoa(JSON.stringify({ sub: '42', roles: ['SELLER'], revision })).replace(/=/g, '') + '.signature';
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });
}

const product = {
  product_id: 7, user_id: 42, user_product_id: 1, name: 'Товар',
  price: 120, count: 2, count_of_sales: 3, average_estimation: 4.5,
};
const tokens = (revision = 0) => ({ access_token: jwt(revision), refresh_token: 'refresh-' + revision });

function storage() {
  const values = new Map<string, string>();
  return {
    getItem: (key: string) => values.get(key) ?? null,
    setItem: (key: string, value: string) => { values.set(key, value); },
    removeItem: (key: string) => { values.delete(key); },
  };
}

describe('API authentication and refresh rotation', () => {
  it('uses snake_case registration and restores the session after reload', async () => {
    const saved = storage();
    const fetcher = vi.fn<typeof fetch>(async () => json(tokens()));
    const client = new ApiClient('/api', saved, fetcher);
    await client.register({ firstName: 'Иван', lastName: 'Иванов', age: 25, email: 'ivan@example.test', password: 'test-password' });
    expect(JSON.parse(String(fetcher.mock.calls[0][1]?.body))).toEqual({
      first_name: 'Иван', last_name: 'Иванов', age: 25, email: 'ivan@example.test', password: 'test-password',
    });
    expect(new ApiClient('/api', saved, fetcher).getSession()?.user.userId).toBe(42);
    expect(saved.getItem('seller-cabinet.session')).not.toContain('test-password');
  });

  it('rotates a one-use refresh token once for concurrent unauthorized requests', async () => {
    let finishRefresh!: (response: Response) => void;
    const refresh = new Promise<Response>((resolve) => { finishRefresh = resolve; });
    let refreshCount = 0;
    const fetcher = vi.fn<typeof fetch>(async (input, init) => {
      if (String(input).endsWith('/auth/login')) return json(tokens());
      if (String(input).endsWith('/auth/refresh')) { refreshCount++; return refresh; }
      const authorization = new Headers(init?.headers).get('Authorization');
      return authorization === 'Bearer ' + jwt(1) ? json([product]) : json({}, 401);
    });
    const client = new ApiClient('/api', storage(), fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    const first = client.getProducts(42);
    const second = client.getProducts(42);
    await vi.waitFor(() => expect(refreshCount).toBe(1));
    finishRefresh(json(tokens(1)));
    const results = await Promise.all([first, second]);
    expect(results[0][0].userId).toBe(42);
    expect(results[1][0].productId).toBe(7);
    expect(refreshCount).toBe(1);
    expect(client.getSession()?.refreshToken).toBe('refresh-1');
  });

  it('does not restore the session when logout happens during refresh', async () => {
    let finishRefresh!: (response: Response) => void;
    const refresh = new Promise<Response>((resolve) => { finishRefresh = resolve; });
    let requestedRefresh = false;
    const fetcher = vi.fn<typeof fetch>(async (input) => {
      if (String(input).endsWith('/auth/login')) return json(tokens());
      if (String(input).endsWith('/auth/refresh')) { requestedRefresh = true; return refresh; }
      return json({}, 401);
    });
    const saved = storage();
    const client = new ApiClient('/api', saved, fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    const request = client.getProducts(42);
    const outcome = expect(request).rejects.toBeInstanceOf(ApiError);
    await vi.waitFor(() => expect(requestedRefresh).toBe(true));
    client.logout();
    finishRefresh(json(tokens(1)));
    await outcome;
    expect(client.getSession()).toBeNull();
    expect(saved.getItem('seller-cabinet.session')).toBeNull();
  });

  it('clears the session when the refresh token is rejected', async () => {
    const fetcher = vi.fn<typeof fetch>(async (input) =>
      String(input).endsWith('/auth/login') ? json(tokens()) : json({ message: 'Токен истек' }, 401));
    const client = new ApiClient('/api', storage(), fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    await expect(client.getProducts(42)).rejects.toBeInstanceOf(ApiError);
    expect(client.getSession()).toBeNull();
  });

  it('keeps the refresh token after a temporary server failure and allows retry', async () => {
    let refreshCount = 0;
    const fetcher = vi.fn<typeof fetch>(async (input, init) => {
      if (String(input).endsWith('/auth/login')) return json(tokens());
      if (String(input).endsWith('/auth/refresh')) {
        refreshCount++;
        return refreshCount === 1 ? json({}, 503) : json(tokens(1));
      }
      return new Headers(init?.headers).get('Authorization') === 'Bearer ' + jwt(1)
        ? json([product]) : json({}, 401);
    });
    const client = new ApiClient('/api', storage(), fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    await expect(client.getProducts(42)).rejects.toBeInstanceOf(ApiError);
    expect(client.getSession()?.refreshToken).toBe('refresh-0');
    expect((await client.getProducts(42))[0].productId).toBe(7);
    expect(refreshCount).toBe(2);
  });

  it('handles 204 responses and sends product fields expected by Spring', async () => {
    const fetcher = vi.fn<typeof fetch>(async (input) =>
      String(input).endsWith('/auth/login') ? json(tokens()) : new Response(null, { status: 204 }));
    const client = new ApiClient('/api', storage(), fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    await client.updateProduct(7, { name: 'Товар', price: 120, count: 2, countOfSales: 3, averageEstimation: 4.5 });
    expect(JSON.parse(String(fetcher.mock.calls[1][1]?.body))).toEqual({
      name: 'Товар', price: 120, count: 2, count_of_sales: 3, average_estimation: 4.5,
    });
    await client.deleteProduct(7);
  });

  it('treats a missing seller statistics row as an empty state', async () => {
    const fetcher = vi.fn<typeof fetch>(async (input) =>
      String(input).endsWith('/auth/login') ? json(tokens()) : new Response(null, { status: 404 }));
    const client = new ApiClient('/api', storage(), fetcher);
    await client.login({ email: 'ivan@example.test', password: 'password' });
    expect(await client.getSellerStats(42)).toBeNull();
  });

  it('maps validation field names from snake_case to form names', async () => {
    const error = await responseError(json({
      message: 'Ошибка валидации',
      fields: { first_name: ['Введите имя'], averageEstimation: ['От 1 до 5'] },
    }, 400));
    expect(error.fields).toEqual({ firstName: 'Введите имя', averageEstimation: 'От 1 до 5' });
  });
});
