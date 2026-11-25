import { useId } from 'react';
import type { InputHTMLAttributes } from 'react';

interface FieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  hint?: string;
}

export function Field({ label, error, hint, id, className = '', ...props }: FieldProps) {
  const generatedId = useId();
  const inputId = id || generatedId;
  const descriptionId = inputId + '-description';
  return (
    <div className={['field', className].join(' ')}>
      <label htmlFor={inputId}>{label}{props.required ? <span aria-hidden="true"> *</span> : null}</label>
      <input {...props} id={inputId} aria-invalid={Boolean(error)} aria-describedby={error || hint ? descriptionId : undefined} />
      {error || hint ? <span id={descriptionId} className={error ? 'field__error' : 'field__hint'}>{error || hint}</span> : null}
    </div>
  );
}
