export class ApiError extends Error {
  readonly status: number;
  readonly code?: string;
  readonly fields: Record<string, string>;

  constructor(message: string, status = 0, code?: string, fields: Record<string, string> = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.code = code;
    this.fields = fields;
  }
}

export function errorMessage(error: unknown): string {
  if (error instanceof Error) return error.message;
  return 'Не удалось выполнить действие. Попробуйте ещё раз.';
}

export function isAbortError(error: unknown): boolean {
  return error instanceof Error && error.name === 'AbortError';
}

export async function responseError(response: Response): Promise<ApiError> {
  const messages: Record<number, string> = {
    400: 'Проверьте данные запроса.',
    401: 'Войдите в аккаунт повторно.',
    403: 'Недостаточно прав для этого действия.',
    404: 'Запись не найдена.',
    409: 'Эта запись уже существует.',
    500: 'Ошибка сервера. Попробуйте ещё раз.',
    502: 'Сервис временно недоступен. Проверьте запуск бэкенда.',
    503: 'Сервис временно недоступен. Попробуйте позже.',
    504: 'Сервис не ответил вовремя. Попробуйте ещё раз.',
  };
  let body: Record<string, unknown> = {};
  try {
    const value: unknown = await response.json();
    if (value && typeof value === 'object') body = value as Record<string, unknown>;
  } catch {
  }
  const fields: Record<string, string> = {};
  if (body.fields && typeof body.fields === 'object') {
    for (const [name, errors] of Object.entries(body.fields)) {
      const field = name.replace(/_([a-z])/g, (_, letter: string) => letter.toUpperCase());
      if (Array.isArray(errors)) fields[field] = errors.filter((item) => typeof item === 'string').join(' ');
    }
  }
  const message = typeof body.message === 'string' && body.message.trim()
    ? body.message
    : messages[response.status] || 'Не удалось выполнить запрос.';
  return new ApiError(message, response.status, typeof body.code === 'string' ? body.code : undefined, fields);
}
