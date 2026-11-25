import type { SellerStats } from '../../api/types';
import { count, money, rating } from '../../lib/format';
import { MetricCard } from '../ui/MetricCard';

export function StatsSummary({ stats }: { stats: SellerStats }) {
  return (
    <div className="metrics-grid" aria-label="Основные показатели">
      <MetricCard label="Товаров в каталоге" value={count(stats.totalProducts)} />
      <MetricCard label="Продано единиц" value={count(stats.totalSales)} />
      <MetricCard label="Стоимость остатков" value={money(stats.totalStockValue)} />
      <MetricCard label="Средняя оценка" value={stats.totalProducts > 0 ? rating(stats.averageEstimation) + ' / 5' : '—'} />
    </div>
  );
}
