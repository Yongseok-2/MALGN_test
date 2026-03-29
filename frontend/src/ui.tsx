import type { ReactNode } from 'react'

export function EmptyState({
  title,
  description,
  actions,
}: {
  title: string
  description: string
  actions?: ReactNode
}) {
  return (
    <section className="panel empty-state">
      <h3>{title}</h3>
      <p>{description}</p>
      {actions ? <div className="card-actions">{actions}</div> : null}
    </section>
  )
}

export function Pagination({
  page,
  totalPages,
  onPageChange,
}: {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}) {
  if (totalPages <= 1) return null
  const visible = Array.from({ length: totalPages }, (_, index) => index).slice(
    Math.max(0, page - 2),
    Math.min(totalPages, page + 3),
  )

  return (
    <div className="pagination">
      <button className="ghost-button" type="button" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
        이전
      </button>
      <div className="pagination-pages">
        {visible.map((value) => (
          <button
            key={value}
            type="button"
            className={`page-button${value === page ? ' active' : ''}`}
            onClick={() => onPageChange(value)}
          >
            {value + 1}
          </button>
        ))}
      </div>
      <button
        className="ghost-button"
        type="button"
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        다음
      </button>
    </div>
  )
}
