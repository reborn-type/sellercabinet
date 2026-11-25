import { describe, expect, it } from 'vitest';
import { productInput, validateProduct } from './productForm';

const valid = { name: 'Товар', price: '125,50', count: '0', countOfSales: '2147483647', averageEstimation: '4,25' };

describe('Product form values', () => {
  it('supports decimal commas and the backend integer range', () => {
    expect(validateProduct(valid)).toEqual({});
    expect(productInput(valid)).toEqual({
      name: 'Товар', price: 125.5, count: 0, countOfSales: 2147483647, averageEstimation: 4.25,
    });
  });

  it('rejects empty inventory and ratings outside the backend range', () => {
    const errors = validateProduct({ ...valid, count: '', averageEstimation: '0', price: '1.234' });
    expect(Object.keys(errors).sort()).toEqual(['averageEstimation', 'count', 'price']);
  });
});
