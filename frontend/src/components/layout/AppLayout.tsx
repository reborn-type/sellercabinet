import { NavLink, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { Button } from '../ui/Button';

export function AppLayout() {
  const { session, logout } = useAuth();
  const location = useLocation();
  if (!session) return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
  const admin = session.user.roles.includes('ADMIN');

  return (
    <div className="app">
      <header className="app-header">
        <div className="app-header__inner">
          <NavLink to="/products" className="brand">Кабинет продавца</NavLink>
          <nav className="main-nav" aria-label="Основная навигация">
            <NavLink to="/products" className={({ isActive }) => isActive ? 'nav-link nav-link--active' : 'nav-link'}>Товары</NavLink>
            <NavLink to="/analytics" className={({ isActive }) => isActive ? 'nav-link nav-link--active' : 'nav-link'}>Аналитика</NavLink>
          </nav>
          <div className="account">
            <div className="account__identity">
              <strong>{session.user.email || 'Пользователь #' + session.user.userId}</strong>
              <span>{admin ? 'Администратор' : 'Продавец'} · ID {session.user.userId}</span>
            </div>
            <Button variant="quiet" onClick={logout}>Выйти</Button>
          </div>
        </div>
      </header>
      <main className="main-content"><Outlet /></main>
    </div>
  );
}
