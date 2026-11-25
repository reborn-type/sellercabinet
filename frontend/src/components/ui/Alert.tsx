import type { PropsWithChildren } from 'react';

export function Alert({ children, tone = 'error' }: PropsWithChildren<{ tone?: 'error' | 'success' | 'info' }>) {
  return <div className={'alert alert--' + tone} role={tone === 'error' ? 'alert' : 'status'}>{children}</div>;
}
