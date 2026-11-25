export function MetricCard({ label, value, detail }: { label: string; value: string; detail?: string }) {
  return <article className="metric-card"><h2>{label}</h2><p className="metric-card__value">{value}</p>{detail ? <p className="muted">{detail}</p> : null}</article>;
}
