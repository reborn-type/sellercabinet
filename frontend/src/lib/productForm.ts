import type { ProductInput } from '../api/types';

export interface ProductFormValues {
  name: string;
  price: string;
  count: string;
  countOfSales: string;
  averageEstimation: string;
}

export function validateProduct(values: ProductFormValues): Record<string, string> {
  const errors: Record<string, string> = {};
  const price = Number(values.price.replace(',', '.'));
  const estimation = Number(values.averageEstimation.replace(',', '.'));
  if (values.name.trim().length < 2 || values.name.trim().length > 200) {
    errors.name = 'Введите название от 2 до 200 символов.';
  }
  if (!/^\d{1,12}([.,]\d{1,2})?$/.test(values.price) || price <= 0) {
    errors.price = 'Введите цену больше нуля, до 12 цифр и 2 знаков после запятой.';
  }
  for (const field of ['count', 'countOfSales'] as const) {
    const value = Number(values[field]);
    if (!values[field].trim() || !Number.isInteger(value) || value < 0 || value > 2147483647) {
      errors[field] = 'Введите целое число от 0 до 2 147 483 647.';
    }
  }
  if (!/^\d([.,]\d{1,2})?$/.test(values.averageEstimation) || estimation < 1 || estimation > 5) {
    errors.averageEstimation = 'Введите оценку от 1 до 5, до 2 знаков после запятой.';
  }
  return errors;
}

export function productInput(values: ProductFormValues): ProductInput {
  return {
    name: values.name.trim(),
    price: Number(values.price.replace(',', '.')),
    count: Number(values.count),
    countOfSales: Number(values.countOfSales),
    averageEstimation: Number(values.averageEstimation.replace(',', '.')),
  };
}
