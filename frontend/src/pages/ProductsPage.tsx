import { useCallback, useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import type { Product } from '../api/types';
import { useAuth } from '../auth/AuthContext';
import { ProductCard } from '../components/products/ProductCard';
import { ProductForm } from '../components/products/ProductForm';
import { DeleteProductDialog } from '../components/products/DeleteProductDialog';
import { Alert } from '../components/ui/Alert';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { Field } from '../components/ui/Field';
import { useResource } from '../hooks/useResource';
import { count } from '../lib/format';

export function ProductsPage() {
  const { session } = useAuth();
  const [params, setParams] = useSearchParams();
  const admin = Boolean(session?.user.roles.includes('ADMIN'));
  const ownId = session?.user.userId;
  const requestedId = Number(params.get('userId'));
  const filterId = admin && Number.isSafeInteger(requestedId) && requestedId > 0 ? requestedId : undefined;
  const userId = admin ? filterId : ownId;
  const [search, setSearch] = useState('');
  const filterQuery = params.get('userId') || '';
  const [filterInput, setFilterInput] = useState(filterQuery);
  const [filterError, setFilterError] = useState('');
  const [editor, setEditor] = useState<{ product: Product | null } | null>(null);
  const [deleting, setDeleting] = useState<Product | null>(null);
  const [notice, setNotice] = useState('');
  const load = useCallback((signal: AbortSignal) => api.getProducts(userId, signal), [userId]);
  const { data, loading, error, reload } = useResource(load);
  const products = data ?? [];
  const visible = products.filter((product) => product.name.toLocaleLowerCase('ru').includes(search.trim().toLocaleLowerCase('ru')));
  const canCreate = session?.user.roles.includes('SELLER');

  useEffect(() => { setFilterInput(filterQuery); }, [filterQuery]);

  function filter(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!filterInput.trim()) { setParams({}); setFilterError(''); return; }
    const value = Number(filterInput);
    if (!Number.isSafeInteger(value) || value <= 0) {
      setFilterError('Введите положительный целый ID.');
      return;
    }
    setFilterError('');
    setParams({ userId: String(value) });
  }

  function saved() {
    setEditor(null);
    setNotice('Товар сохранён. Обновление аналитики может занять немного времени.');
    reload();
  }

  return (
    <>
      <div className="page-heading">
        <div><h1>{admin ? 'Товары продавцов' : 'Мои товары'}</h1></div>
        <div className="heading-actions">
          <Button variant="secondary" onClick={reload} disabled={loading}>Обновить</Button>
          {canCreate ? <Button onClick={() => setEditor({ product: null })}>Добавить товар</Button> : null}
        </div>
      </div>
      {notice ? <Alert tone="success">{notice}</Alert> : null}
      <div className="toolbar">
        <Field label="Поиск по названию" type="search" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Название товара" className="toolbar__search" />
        {admin ? (
          <form className="seller-filter" onSubmit={filter}>
            <Field label="ID продавца" type="number" min={1} step={1} value={filterInput}
              onChange={(e) => setFilterInput(e.target.value)} error={filterError} placeholder="Все продавцы" />
            <Button type="submit" variant="secondary">Показать</Button>
            {filterId ? <Button variant="quiet" onClick={() => { setFilterInput(''); setParams({}); }}>Сбросить</Button> : null}
          </form>
        ) : null}
      </div>
      {error ? <Alert>{error}</Alert> : null}
      {loading ? <div className="loading-state" role="status">Загрузка товаров…</div> : null}
      {!loading && !error ? (
        <>
          <p className="result-count">Найдено товаров · {count(visible.length)}</p>
          {visible.length ? (
            <div className="product-grid">
              {visible.map((product) => (
                <ProductCard key={product.productId} product={product} showOwner={admin}
                  canEdit={admin || product.userId === ownId} onEdit={(product) => setEditor({ product })} onDelete={setDeleting} />
              ))}
            </div>
          ) : (
            <EmptyState title={search ? 'Товары не найдены' : 'Пока нет товаров'}>
              <p>{search ? 'Попробуйте другое название.' : canCreate ? 'Добавьте первый товар в свой каталог.' : 'У выбранного продавца пока нет товаров.'}</p>
            </EmptyState>
          )}
        </>
      ) : null}
      {editor ? <ProductForm product={editor.product} onClose={() => setEditor(null)} onSaved={saved} /> : null}
      {deleting ? <DeleteProductDialog product={deleting} onClose={() => setDeleting(null)}
        onDeleted={() => { setDeleting(null); setNotice('Товар удалён. Показатели аналитики обновляются.'); reload(); }} /> : null}
    </>
  );
}
