const moneyFormatter = new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 2 });
const countFormatter = new Intl.NumberFormat('ru-RU');
const ratingFormatter = new Intl.NumberFormat('ru-RU', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const dateFormatter = new Intl.DateTimeFormat('ru-RU', { dateStyle: 'medium', timeStyle: 'short' });

export const money = (value: number): string => moneyFormatter.format(value);
export const count = (value: number): string => countFormatter.format(value);
export const rating = (value: number): string => ratingFormatter.format(value);
export function dateTime(value: string): string {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? '—' : dateFormatter.format(date);
}
