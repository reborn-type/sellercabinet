import { useState } from 'react';
import { api } from '../../api/client';
import { errorMessage } from '../../api/errors';
import type { Product } from '../../api/types';
import { Alert } from '../ui/Alert';
import { Button } from '../ui/Button';
import { Modal } from '../ui/Modal';

export function DeleteProductDialog({ product, onClose, onDeleted }: {
  product: Product; onClose: () => void; onDeleted: () => void;
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  async function remove() {
    if (busy) return;
    setBusy(true);
    setError('');
    try {
      await api.deleteProduct(product.productId);
      onDeleted();
    } catch (reason) {
      setError(errorMessage(reason));
      setBusy(false);
    }
  }
  return (
    <Modal title="Удалить товар?" onClose={onClose} busy={busy}>
      <p className="dialog-description">«{product.name}» будет удалён из каталога.</p>
      {error ? <Alert>{error}</Alert> : null}
      <div className="form-actions">
        <Button variant="secondary" onClick={onClose} disabled={busy}>Отмена</Button>
        <Button variant="danger" onClick={remove} busy={busy}>{busy ? 'Удаление…' : 'Удалить товар'}</Button>
      </div>
    </Modal>
  );
}
