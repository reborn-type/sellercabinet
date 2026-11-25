import type { Product } from '../../api/types';
import { count, money, rating } from '../../lib/format';
import { Button } from '../ui/Button';

interface ProductCardProps {
  product: Product;
  showOwner: boolean;
  canEdit: boolean;
  onEdit: (product: Product) => void;
  onDelete: (product: Product) => void;
}

export function ProductCard({ product, showOwner, canEdit, onEdit, onDelete }: ProductCardProps) {
  return (
    <article className="product-card">
      <div className="product-card__top">
        <span className="muted">Товар #{product.productId}</span>
        <span className={product.count > 0 ? 'stock-label' : 'stock-label stock-label--empty'}>
          {product.count > 0 ? 'В наличии' : 'Нет в наличии'}
        </span>
      </div>
      <h2>{product.name}</h2>
      {showOwner ? <p className="muted">Продавец #{product.userId}</p> : null}
      <p className="product-card__price">{money(product.price)}</p>
      <dl className="product-details">
        <div><dt>На складе</dt><dd>{count(product.count)} шт.</dd></div>
        <div><dt>Продано</dt><dd>{count(product.countOfSales)} шт.</dd></div>
        <div><dt>Средняя оценка</dt><dd>{rating(product.averageEstimation)} / 5</dd></div>
      </dl>
      {canEdit ? (
        <div className="product-card__actions">
          <Button variant="secondary" onClick={() => onEdit(product)} aria-label={'Изменить ' + product.name}>Изменить</Button>
          <Button variant="quiet" onClick={() => onDelete(product)} aria-label={'Удалить ' + product.name}>Удалить</Button>
        </div>
      ) : null}
    </article>
  );
}
