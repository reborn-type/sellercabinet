import { useEffect, useId, useRef } from 'react';
import type { PropsWithChildren } from 'react';
import { Button } from './Button';

interface ModalProps extends PropsWithChildren {
  title: string;
  onClose: () => void;
  busy?: boolean;
}

export function Modal({ title, onClose, busy = false, children }: ModalProps) {
  const dialog = useRef<HTMLDialogElement>(null);
  const titleId = useId();
  useEffect(() => {
    const element = dialog.current;
    element?.showModal();
    return () => element?.close();
  }, []);

  return (
    <dialog ref={dialog} className="modal" aria-labelledby={titleId}
      onCancel={(event) => { event.preventDefault(); if (!busy) onClose(); }}
      onClick={(event) => { if (event.target === event.currentTarget && !busy) onClose(); }}>
      <div className="modal__surface">
        <header className="modal__header">
          <h2 id={titleId}>{title}</h2>
          <Button variant="quiet" onClick={onClose} disabled={busy}>Закрыть</Button>
        </header>
        {children}
      </div>
    </dialog>
  );
}
