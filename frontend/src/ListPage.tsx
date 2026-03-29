import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import type { ContentQuery, ContentSummary, PageData } from './types'
import { dateText, errorText, normalizePage, normalizeSummary } from './utils'
import { EmptyState, Pagination } from './ui'

type Props = {
  title: string
  subtitle: string
  fetcher: (query: ContentQuery) => Promise<unknown>
  hideTypeFilter?: boolean
  disableSearch?: boolean
}

const DEFAULT_QUERY: ContentQuery = {
  page: 0,
  size: 10,
  sort: 'createdDate,desc',
  type: 'TITLE',
  keyword: '',
}

export function ListPage({
  title,
  subtitle,
  fetcher,
  hideTypeFilter = false,
  disableSearch = false,
}: Props) {
  const navigate = useNavigate()
  const [draft, setDraft] = useState(DEFAULT_QUERY)
  const [query, setQuery] = useState(DEFAULT_QUERY)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [data, setData] = useState<PageData<ContentSummary>>({
    items: [],
    page: 0,
    size: DEFAULT_QUERY.size,
    totalPages: 0,
    totalElements: 0,
  })

  useEffect(() => {
    let active = true
    ;(async () => {
      setLoading(true)
      setError('')
      try {
        const raw = await fetcher(query)
        if (!active) return
        setData(normalizePage(raw, normalizeSummary))
      } catch (cause) {
        if (!active) return
        setError(errorText(cause))
      } finally {
        if (active) setLoading(false)
      }
    })()
    return () => {
      active = false
    }
  }, [fetcher, query])

  const apply = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setQuery({ ...draft, page: 0 })
  }

  const pageMove = (next: number) => {
    if (next < 0 || next >= data.totalPages) return
    setQuery((current) => ({ ...current, page: next }))
  }

  return (
    <div className="page-stack">
      <section className="hero-card">
        <div>
          <p className="eyebrow">Content</p>
          <h2>{title}</h2>
          <p className="hero-copy">{subtitle}</p>
        </div>
        <div className="hero-actions">
          <Link className="primary-button" to="/new">
            새 글 작성
          </Link>
        </div>
      </section>

      {!disableSearch ? (
        <section className="panel">
          <div className="search-header">
            <div className="search-total">
              <span>전체 글수</span>
              <strong>{data.totalElements}</strong>
            </div>
          <form className="search-bar" onSubmit={apply}>
            {!hideTypeFilter ? (
              <label>
                <span>검색 기준</span>
                <select
                  value={draft.type}
                  onChange={(event) =>
                    setDraft((current) => ({ ...current, type: event.target.value }))
                  }
                >
                  <option value="TITLE">제목</option>
                  <option value="DESCRIPTION">내용</option>
                  <option value="CREATED_BY">작성자</option>
                </select>
              </label>
            ) : null}
            <label className="wide">
              <span>키워드</span>
              <input
                value={draft.keyword || ''}
                onChange={(event) =>
                  setDraft((current) => ({ ...current, keyword: event.target.value }))
                }
                placeholder="검색어를 입력하세요"
              />
            </label>
            <button className="primary-button" type="submit">
              검색
            </button>
          </form>
          </div>
        </section>
      ) : null}

      {error ? <div className="error-banner">{error}</div> : null}

      <section className="content-grid">
        {loading ? (
          Array.from({ length: 6 }).map((_, index) => (
            <article key={index} className="content-card skeleton">
              <div className="skeleton-line title" />
              <div className="skeleton-line" />
              <div className="skeleton-line short" />
            </article>
          ))
        ) : data.items.length ? (
          data.items.map((item) => (
            <article
              key={item.id}
              className="content-card list-row-card"
              role="button"
              tabIndex={0}
              onClick={() => navigate(`/content/${item.id}`)}
              onKeyDown={(event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                  event.preventDefault()
                  navigate(`/content/${item.id}`)
                }
              }}
            >
              <div className="list-row-main">
                <div className="list-row-text">
                  <h3 className="list-title">{item.title}</h3>
                  <p className="list-summary">{item.description || '설명이 없습니다.'}</p>
                </div>
                <div className="list-row-right">
                  <span className="list-id-label">ID</span>
                  <strong className="list-id">{item.id}</strong>
                </div>
              </div>

              <div className="list-row-meta">
                <span className="chip">{item.createdBy || 'unknown'}</span>
                <span className="chip subtle">{dateText(item.createdDate)}</span>
                <span className="chip subtle">조회 {item.viewCount ?? 0}</span>
              </div>
            </article>
          ))
        ) : (
          <EmptyState
            title="게시글이 없습니다"
            description="검색 조건을 바꾸거나 새 글을 작성해 보세요."
            actions={
              <Link className="primary-button" to="/new">
                새 글 작성
              </Link>
            }
          />
        )}
      </section>

      <Pagination page={data.page} totalPages={data.totalPages} onPageChange={pageMove} />
    </div>
  )
}
