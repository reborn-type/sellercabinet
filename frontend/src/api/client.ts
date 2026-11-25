import { ApiError, responseError } from './errors';
import type { CurrentUser, LoginInput, Product, ProductInput, RegisterInput, SellerStats, Session, TokenPair } from './types';

type StorageLike = Pick<Storage, 'getItem' | 'setItem' | 'removeItem'>;
type Row = Record<string, unknown>;
const SESSION_KEY = 'seller-cabinet.session';

function userFromToken(token: string, email: string): CurrentUser {
  try {
    const payload = token.split('.')[1];
    if (!payload) throw new Error('Missing JWT payload');
    const encoded = payload.replace(/-/g, '+').replace(/_/g, '/');
    const bytes = Uint8Array.from(atob(encoded.padEnd(Math.ceil(encoded.length / 4) * 4, '=')), (char) => char.charCodeAt(0));
    const claims = JSON.parse(new TextDecoder().decode(bytes)) as Row;
    const userId = Number(claims.sub);
    const roles = Array.isArray(claims.roles)
      ? claims.roles.filter((role): role is string => typeof role === 'string')
      : [];
    if (!Number.isSafeInteger(userId) || userId <= 0 || !roles.some((role) => role === 'SELLER' || role === 'ADMIN')) {
      throw new Error('Unsupported JWT identity');
    }
    return { userId, roles, email };
  } catch {
    throw new ApiError('Сервер вернул некорректный токен авторизации.', 502);
  }
}

function tokenPair(value: unknown): TokenPair {
  const row = value as Row | null;
  if (!row || typeof row.access_token !== 'string' || !row.access_token
    || typeof row.refresh_token !== 'string' || !row.refresh_token) {
    throw new ApiError('Сервер вернул некорректный ответ авторизации.', 502);
  }
  return { accessToken: row.access_token, refreshToken: row.refresh_token };
}

function numberField(row: Row, snake: string, camel = snake): number {
  const raw = row[snake] ?? row[camel];
  const value = typeof raw === 'number' ? raw : Number(raw);
  if (raw == null || raw === '' || !Number.isFinite(value)) {
    throw new ApiError('Сервер вернул некорректные данные.', 502);
  }
  return value;
}

function productFromWire(row: Row): Product {
  return {
    productId: numberField(row, 'product_id', 'productId'),
    userId: numberField(row, 'user_id', 'userId'),
    userProductId: row.user_product_id == null && row.userProductId == null
      ? null : numberField(row, 'user_product_id', 'userProductId'),
    name: String(row.name ?? ''),
    price: numberField(row, 'price'),
    count: numberField(row, 'count'),
    countOfSales: numberField(row, 'count_of_sales', 'countOfSales'),
    averageEstimation: numberField(row, 'average_estimation', 'averageEstimation'),
  };
}

function statsFromWire(row: Row): SellerStats {
  return {
    userId: numberField(row, 'user_id', 'userId'),
    totalProducts: numberField(row, 'total_products', 'totalProducts'),
    totalSales: numberField(row, 'total_sales', 'totalSales'),
    totalStockValue: numberField(row, 'total_stock_value', 'totalStockValue'),
    averageEstimation: numberField(row, 'average_estimation', 'averageEstimation'),
    updatedAt: String(row.updated_at ?? row.updatedAt ?? ''),
  };
}

export class ApiClient {
  private readonly baseUrl: string;
  private readonly storage?: StorageLike;
  private readonly fetcher: typeof fetch;
  private session: Session | null = null;
  private generation = 0;
  private refreshPromise: Promise<void> | null = null;
  private readonly listeners = new Set<() => void>();

  constructor(baseUrl: string, storage?: StorageLike, fetcher: typeof fetch = fetch.bind(globalThis)) {
    this.baseUrl = baseUrl.replace(/\/$/, '');
    this.storage = storage;
    this.fetcher = fetcher;
    try {
      const saved = JSON.parse(storage?.getItem(SESSION_KEY) || 'null') as Row | null;
      if (saved && typeof saved.accessToken === 'string' && typeof saved.refreshToken === 'string') {
        const email = typeof saved.email === 'string' ? saved.email : '';
        this.session = {
          accessToken: saved.accessToken,
          refreshToken: saved.refreshToken,
          user: userFromToken(saved.accessToken, email),
        };
      }
    } catch {
      this.session = null;
      try { storage?.removeItem(SESSION_KEY); } catch { /* Storage may be unavailable. */ }
    }
  }

  getSession = (): Session | null => this.session;

  subscribe = (listener: () => void): (() => void) => {
    this.listeners.add(listener);
    return () => { this.listeners.delete(listener); };
  };

  logout = (): void => {
    this.generation++;
    this.refreshPromise = null;
    this.commitSession(null);
  };

  private commitSession(session: Session | null): void {
    this.session = session;
    try {
      if (session) {
        this.storage?.setItem(SESSION_KEY, JSON.stringify({
          accessToken: session.accessToken,
          refreshToken: session.refreshToken,
          email: session.user.email,
        }));
      } else {
        this.storage?.removeItem(SESSION_KEY);
      }
    } catch {
    }
    for (const listener of this.listeners) listener();
  }

  private async send(path: string, init: RequestInit, token?: string): Promise<Response> {
    const headers = new Headers(init.headers);
    headers.set('Accept', 'application/json');
    if (init.body != null) headers.set('Content-Type', 'application/json');
    if (token) headers.set('Authorization', 'Bearer ' + token);
    try {
      return await this.fetcher(this.baseUrl + path, { ...init, headers });
    } catch (error) {
      if (init.signal?.aborted) throw error;
      throw new ApiError('Не удалось связаться с сервером. Проверьте соединение и запуск бэкенда.');
    }
  }

  private async json<T>(response: Response): Promise<T> {
    if (!response.ok) throw await responseError(response);
    if (response.status === 204) return undefined as T;
    const text = await response.text();
    if (!text) return undefined as T;
    try {
      return JSON.parse(text) as T;
    } catch {
      throw new ApiError('Сервер вернул некорректный JSON.', response.status);
    }
  }

  private async refreshSession(): Promise<void> {
    if (this.refreshPromise) return this.refreshPromise;
    const session = this.session;
    const generation = this.generation;
    if (!session) throw new ApiError('Войдите в аккаунт повторно.', 401);

    const task = (async () => {
      try {
        const response = await this.send('/auth/refresh', {
          method: 'POST',
          body: JSON.stringify({ refresh_token: session.refreshToken }),
        });
        const tokens = tokenPair(await this.json<unknown>(response));
        if (this.generation !== generation) {
          throw new ApiError('Сессия изменилась. Повторите действие.', 401);
        }
        this.commitSession({ ...tokens, user: userFromToken(tokens.accessToken, session.user.email) });
      } catch (error) {
        const invalidToken = error instanceof ApiError && [400, 401, 403].includes(error.status);
        if (this.generation === generation && invalidToken) this.logout();
        throw error;
      }
    })();

    this.refreshPromise = task;
    try {
      await task;
    } finally {
      if (this.refreshPromise === task) this.refreshPromise = null;
    }
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const session = this.session;
    const generation = this.generation;
    if (!session) throw new ApiError('Войдите в аккаунт повторно.', 401);
    let response = await this.send(path, init, session.accessToken);
    if (response.status === 401) {
      if (generation !== this.generation || !this.session) {
        throw new ApiError('Сессия изменилась. Повторите действие.', 401);
      }
      if (this.session.accessToken === session.accessToken) await this.refreshSession();
      if (generation !== this.generation || !this.session) {
        throw new ApiError('Войдите в аккаунт повторно.', 401);
      }
      response = await this.send(path, init, this.session.accessToken);
      if (response.status === 401 && generation === this.generation) this.logout();
    }
    return this.json<T>(response);
  }

  private async authenticate(path: string, body: Row, email: string): Promise<void> {
    const generation = this.generation;
    const response = await this.send(path, { method: 'POST', body: JSON.stringify(body) });
    const tokens = tokenPair(await this.json<unknown>(response));
    if (generation !== this.generation) throw new ApiError('Сессия изменилась. Повторите вход.', 401);
    const user = userFromToken(tokens.accessToken, email);
    this.generation++;
    this.refreshPromise = null;
    this.commitSession({ ...tokens, user });
  }

  login(input: LoginInput): Promise<void> {
    return this.authenticate('/auth/login', { email: input.email, password: input.password }, input.email);
  }

  register(input: RegisterInput): Promise<void> {
    return this.authenticate('/auth/register', {
      first_name: input.firstName,
      last_name: input.lastName,
      age: input.age,
      email: input.email,
      password: input.password,
    }, input.email);
  }

  async getProducts(userId?: number, signal?: AbortSignal): Promise<Product[]> {
    const path = userId == null ? '/products' : '/products/by-seller/' + userId;
    const rows = await this.request<Row[]>(path, { signal });
    if (!Array.isArray(rows)) throw new ApiError('Сервер вернул некорректный список товаров.', 502);
    return rows.map(productFromWire);
  }

  async createProduct(input: ProductInput): Promise<Product> {
    const row = await this.request<Row>('/products', {
      method: 'POST', body: JSON.stringify(this.productBody(input)),
    });
    return productFromWire(row);
  }

  updateProduct(productId: number, input: ProductInput): Promise<void> {
    return this.request('/products/' + productId, {
      method: 'PUT', body: JSON.stringify(this.productBody(input)),
    });
  }

  deleteProduct(productId: number): Promise<void> {
    return this.request('/products/' + productId, { method: 'DELETE' });
  }

  private productBody(input: ProductInput): Row {
    return {
      name: input.name,
      price: input.price,
      count: input.count,
      count_of_sales: input.countOfSales,
      average_estimation: input.averageEstimation,
    };
  }

  async getSellerStats(userId: number, signal?: AbortSignal): Promise<SellerStats | null> {
    try {
      return statsFromWire(await this.request<Row>('/analytics/sellers/' + userId, { signal }));
    } catch (error) {
      if (error instanceof ApiError && error.status === 404) return null;
      throw error;
    }
  }

  async getAllStats(signal?: AbortSignal): Promise<SellerStats[]> {
    const rows = await this.request<Row[]>('/analytics/sellers', { signal });
    if (!Array.isArray(rows)) throw new ApiError('Сервер вернул некорректную статистику.', 502);
    return rows.map(statsFromWire);
  }
}

function browserStorage(): StorageLike | undefined {
  try {
    return typeof window === 'undefined' ? undefined : window.sessionStorage;
  } catch {
    return undefined;
  }
}

export const api = new ApiClient(import.meta.env.VITE_API_BASE_URL || '/api', browserStorage());
