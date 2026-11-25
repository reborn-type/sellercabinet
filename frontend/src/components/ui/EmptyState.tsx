import type { PropsWithChildren } from 'react';

export function EmptyState({ title, children }: PropsWithChildren<{ title: string }>) {
  return <div className="empty-state"><h2>{title}</h2>{children ? <div>{children}</div> : null}</div>;
}
