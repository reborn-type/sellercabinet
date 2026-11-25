import type { ButtonHTMLAttributes } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'quiet';
  busy?: boolean;
}

export function Button({ variant = 'primary', busy = false, disabled, className = '', children, type = 'button', ...props }: ButtonProps) {
  return (
    <button
      {...props}
      type={type}
      disabled={disabled || busy}
      aria-busy={busy || undefined}
      className={['button', 'button--' + variant, className].join(' ')}
    >
      {children}
    </button>
  );
}
