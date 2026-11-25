import { Link } from 'react-router-dom';
import type { SellerStats } from '../../api/types';
import { count, dateTime, money, rating } from '../../lib/format';

export function StatsTable({ stats }: { stats: SellerStats[] }) {
  return (
    <div className="table-card">
      <div className="table-card__heading"><h2>По продавцам</h2></div>
      <div className="table-scroll" tabIndex={0} role="region" aria-label="Статистика продавцов">
        <table>
          <caption className="sr-only">Статистика продавцов</caption>
          <thead><tr><th scope="col">Продавец</th><th scope="col">Товаров</th><th scope="col">Продано</th><th scope="col">Остатки</th><th scope="col">Оценка</th><th scope="col">Обновлено</th><th scope="col"><span className="sr-only">Действия</span></th></tr></thead>
          <tbody>
            {stats.map((row) => (
              <tr key={row.userId}>
                <th scope="row"><Link to={'/analytics?userId=' + row.userId}>#{row.userId}</Link></th>
                <td>{count(row.totalProducts)}</td>
                <td>{count(row.totalSales)}</td>
                <td>{money(row.totalStockValue)}</td>
                <td>{row.totalProducts > 0 ? rating(row.averageEstimation) : '—'}</td>
                <td className="table-date">{dateTime(row.updatedAt)}</td>
                <td><Link to={'/products?userId=' + row.userId} aria-label={'Товары продавца ' + row.userId}>Товары</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
