import { useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { StatsSummary } from '../components/analytics/StatsSummary';
import { StatsTable } from '../components/analytics/StatsTable';
import { Alert } from '../components/ui/Alert';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { MetricCard } from '../components/ui/MetricCard';
import { useResource } from '../hooks/useResource';
import { count, dateTime, money } from '../lib/format';

export function AnalyticsPage() {
  const { session } = useAuth();
  const [params] = useSearchParams();
  const admin = Boolean(session?.user.roles.includes('ADMIN'));
  const ownId = session?.user.userId ?? 0;
  const requestedId = Number(params.get('userId'));
  const selectedId = admin && Number.isSafeInteger(requestedId) && requestedId > 0 ? requestedId : undefined;
  const overview = admin && selectedId == null;
  const userId = selectedId ?? ownId;
  const load = useCallback(async (signal: AbortSignal) => {
    if (overview) return api.getAllStats(signal);
    const stats = await api.getSellerStats(userId, signal);
    return stats ? [stats] : [];
  }, [overview, userId]);
  const { data, loading, error, reload } = useResource(load);
  const stats = data ?? [];
  const current = stats[0];
  const totalProducts = stats.reduce((sum, row) => sum + row.totalProducts, 0);
  const totalSales = stats.reduce((sum, row) => sum + row.totalSales, 0);
  const stockValue = stats.reduce((sum, row) => sum + row.totalStockValue, 0);

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>{overview ? 'Аналитика продавцов' : admin ? 'Продавец #' + userId : 'Моя аналитика'}</h1>
          {!overview && current ? <p className="muted">Обновлено {dateTime(current.updatedAt)}</p> : null}
        </div>
        <div className="heading-actions">
          {admin && !overview ? <Link to="/analytics" className="text-link">Все продавцы</Link> : null}
          <Button variant="secondary" onClick={reload} disabled={loading}>Обновить</Button>
        </div>
      </div>
      {error ? <Alert>{error}</Alert> : null}
      {loading ? <div className="loading-state" role="status">Загрузка аналитики…</div> : null}
      {!loading && !error && !stats.length ? (
        <EmptyState title="Статистика пока не сформирована">
          <p>Показатели появятся после обработки изменений товаров. Попробуйте обновить страницу.</p>
          <Link to="/products" className="text-link">Перейти к товарам</Link>
        </EmptyState>
      ) : null}
      {!loading && !error && stats.length > 0 ? (
        overview ? (
          <>
            <div className="metrics-grid" aria-label="Общие показатели">
              <MetricCard label="Продавцов со статистикой" value={count(stats.length)} />
              <MetricCard label="Товаров в каталоге" value={count(totalProducts)} />
              <MetricCard label="Продано единиц" value={count(totalSales)} />
              <MetricCard label="Стоимость остатков" value={money(stockValue)} />
            </div>
            <StatsTable stats={stats} />
          </>
        ) : current ? (
          <>
            <StatsSummary stats={current} />
            <Link to={admin ? '/products?userId=' + userId : '/products'} className="text-link">Посмотреть товары</Link>
          </>
        ) : null
      ) : null}
    </>
  );
}
