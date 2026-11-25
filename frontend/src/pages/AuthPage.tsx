import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, Navigate, useLocation } from 'react-router-dom';
import { ApiError, errorMessage } from '../api/errors';
import { useAuth } from '../auth/AuthContext';
import { Alert } from '../components/ui/Alert';
import { Button } from '../components/ui/Button';
import { Field } from '../components/ui/Field';

type AuthValues = {
  firstName: string; lastName: string; age: string; email: string; password: string; confirmPassword: string;
};

export function AuthPage({ mode }: { mode: 'login' | 'register' }) {
  const { session, login, register } = useAuth();
  const location = useLocation();
  const registration = mode === 'register';
  const [values, setValues] = useState<AuthValues>({
    firstName: '', lastName: '', age: '', email: '', password: '', confirmPassword: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const from = (location.state as { from?: unknown } | null)?.from;
  const destination = typeof from === 'string' && /^\/(products|analytics)(\?|$)/.test(from) ? from : '/products';
  if (session) return <Navigate to={destination} replace />;

  function change(field: keyof AuthValues, value: string) {
    setValues((previous) => ({ ...previous, [field]: value }));
    setErrors((previous) => ({ ...previous, [field]: '' }));
    setError('');
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (busy) return;
    const validation: Record<string, string> = {};
    const email = values.email.trim();
    if (!/^[^\s@]+@[^\s@]+$/.test(email) || email.length > 200) validation.email = 'Введите корректный email.';
    if (!values.password) validation.password = 'Введите пароль.';
    if (registration) {
      if (values.firstName.trim().length < 2 || values.firstName.trim().length > 50) validation.firstName = 'Имя должно содержать от 2 до 50 символов.';
      if (values.lastName.trim().length < 2 || values.lastName.trim().length > 50) validation.lastName = 'Фамилия должна содержать от 2 до 50 символов.';
      const age = Number(values.age);
      if (!values.age || !Number.isInteger(age) || age < 18 || age > 110) validation.age = 'Укажите возраст от 18 до 110 лет.';
      if (values.password.length < 8 || values.password.length > 72) validation.password = 'Пароль должен содержать от 8 до 72 символов.';
      if (values.password !== values.confirmPassword) validation.confirmPassword = 'Пароли должны совпадать.';
    }
    setErrors(validation);
    if (Object.keys(validation).length) return;
    setBusy(true);
    setError('');
    try {
      if (registration) {
        await register({ firstName: values.firstName.trim(), lastName: values.lastName.trim(), age: Number(values.age), email, password: values.password });
      } else {
        await login({ email, password: values.password });
      }
    } catch (reason) {
      setError(errorMessage(reason));
      if (reason instanceof ApiError) setErrors(reason.fields);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-brand">Кабинет продавца</div>
      <section className={'auth-card' + (registration ? ' auth-card--register' : '')} aria-labelledby="auth-heading">
        <div className="auth-tabs" aria-label="Доступ к аккаунту">
          <Link to="/login" className={!registration ? 'auth-tab auth-tab--active' : 'auth-tab'} aria-current={!registration ? 'page' : undefined}>Вход</Link>
          <Link to="/register" className={registration ? 'auth-tab auth-tab--active' : 'auth-tab'} aria-current={registration ? 'page' : undefined}>Регистрация</Link>
        </div>
        <h1 id="auth-heading">{registration ? 'Создать аккаунт' : 'Вход в кабинет'}</h1>
        <form className="form" onSubmit={submit} noValidate>
          {error ? <Alert>{error}</Alert> : null}
          {registration ? (
            <div className="form-grid">
              <Field label="Имя" autoComplete="given-name" name="firstName" value={values.firstName}
                onChange={(e) => change('firstName', e.target.value)} error={errors.firstName} maxLength={50} required disabled={busy} />
              <Field label="Фамилия" autoComplete="family-name" name="lastName" value={values.lastName}
                onChange={(e) => change('lastName', e.target.value)} error={errors.lastName} maxLength={50} required disabled={busy} />
              <Field label="Возраст" name="age" type="number" min={18} max={110} step={1} value={values.age}
                onChange={(e) => change('age', e.target.value)} error={errors.age} required disabled={busy} />
            </div>
          ) : null}
          <Field label="Email" name="email" type="email" autoComplete="email" value={values.email}
            onChange={(e) => change('email', e.target.value)} error={errors.email} maxLength={200} required disabled={busy} />
          <Field label="Пароль" name="password" type="password" autoComplete={registration ? 'new-password' : 'current-password'}
            value={values.password} onChange={(e) => change('password', e.target.value)} error={errors.password}
            hint={registration ? 'От 8 до 72 символов' : undefined} required disabled={busy} />
          {registration ? <Field label="Повторите пароль" name="confirmPassword" type="password" autoComplete="new-password"
            value={values.confirmPassword} onChange={(e) => change('confirmPassword', e.target.value)} error={errors.confirmPassword} required disabled={busy} /> : null}
          <Button type="submit" busy={busy} className="button--full">{busy ? 'Подождите…' : registration ? 'Зарегистрироваться' : 'Войти'}</Button>
        </form>
      </section>
    </main>
  );
}
