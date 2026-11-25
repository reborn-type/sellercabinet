import { useState } from 'react';
import type { FormEvent } from 'react';
import { api } from '../../api/client';
import { ApiError, errorMessage } from '../../api/errors';
import type { Product } from '../../api/types';
import { productInput, validateProduct } from '../../lib/productForm';
import type { ProductFormValues } from '../../lib/productForm';
import { Alert } from '../ui/Alert';
import { Button } from '../ui/Button';
import { Field } from '../ui/Field';
import { Modal } from '../ui/Modal';

interface ProductFormProps {
  product: Product | null;
  onClose: () => void;
  onSaved: () => void;
}

export function ProductForm({ product, onClose, onSaved }: ProductFormProps) {
  const [values, setValues] = useState<ProductFormValues>({
    name: product?.name ?? '',
    price: product ? String(product.price) : '',
    count: product ? String(product.count) : '0',
    countOfSales: product ? String(product.countOfSales) : '0',
    averageEstimation: product ? String(product.averageEstimation) : '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  function change(field: keyof ProductFormValues, value: string) {
    setValues((previous) => ({ ...previous, [field]: value }));
    setErrors((previous) => ({ ...previous, [field]: '' }));
    setError('');
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (busy) return;
    const validation = validateProduct(values);
    setErrors(validation);
    if (Object.keys(validation).length) return;
    setBusy(true);
    setError('');
    try {
      const input = productInput(values);
      if (product) await api.updateProduct(product.productId, input);
      else await api.createProduct(input);
      onSaved();
    } catch (reason) {
      setError(errorMessage(reason));
      if (reason instanceof ApiError) setErrors(reason.fields);
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal title={product ? 'Изменить товар' : 'Новый товар'} onClose={onClose} busy={busy}>
      <form className="form" onSubmit={submit} noValidate>
        {error ? <Alert>{error}</Alert> : null}
        <Field label="Название" name="name" value={values.name} onChange={(e) => change('name', e.target.value)}
          error={errors.name} required maxLength={200} autoFocus disabled={busy} />
        <div className="form-grid">
          <Field label="Цена, ₽" name="price" inputMode="decimal" value={values.price}
            onChange={(e) => change('price', e.target.value)} error={errors.price} required disabled={busy} />
          <Field label="На складе, шт." name="count" type="number" min={0} max={2147483647} step={1} value={values.count}
            onChange={(e) => change('count', e.target.value)} error={errors.count} required disabled={busy} />
          <Field label="Продано, шт." name="countOfSales" type="number" min={0} max={2147483647} step={1} value={values.countOfSales}
            onChange={(e) => change('countOfSales', e.target.value)} error={errors.countOfSales} required disabled={busy} />
          <Field label="Средняя оценка" name="averageEstimation" inputMode="decimal" placeholder="От 1 до 5"
            value={values.averageEstimation} onChange={(e) => change('averageEstimation', e.target.value)}
            error={errors.averageEstimation} required disabled={busy} />
        </div>
        <div className="form-actions">
          <Button variant="secondary" onClick={onClose} disabled={busy}>Отмена</Button>
          <Button type="submit" busy={busy}>{busy ? 'Сохранение…' : product ? 'Сохранить' : 'Добавить товар'}</Button>
        </div>
      </form>
    </Modal>
  );
}
