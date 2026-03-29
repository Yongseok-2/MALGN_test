import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { listDeletedContents, restoreContents } from './api'
import type { ContentQuery, ContentSummary, PageData, Session } from './types'
import { dateText, errorText, normalizePage, normalizeSummary } from './utils'
import { EmptyState, Pagination } from './ui'

type Props = {
  session: Session | null
  onNotice: (message: string) => void
}

const DEFAULT_QUERY: ContentQuery = {
  page: 0,
  size: 10,
  sort: 'createdDate,desc',
  type: 'TITLE',
  keyword: '',
}

export function DeletedPage({ onNotice }: Props) {
  const navigate = useNavigate()
  const [query, setQuery] = useState(DEFAULT_QUERY)
  const [selected, setSelected] = useState<Set<string>>(new Set())
  const [loading, setLoading] = useState(false)
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
      try {
        const raw = await listDeletedContents(query)
        if (!active) return
        setData(normalizePage(raw, normalizeSummary))
      } catch (cause) {
        if (!active) return
        onNotice(errorText(cause))
      } finally {
        if (active) setLoading(false)
      }
    })()
    return () => {
      active = false
    }
  }, [onNotice, query])

  const restore = async () => {
    if (!selected.size) {
      onNotice('복구할 게시글을 먼저 선택해 주세요.')
      return
    }
    try {
      await restoreContents([...selected])
      setSelected(new Set())
      onNotice('선택한 게시글을 복구했습니다.')
      const raw = await listDeletedContents(query)
      setData(normalizePage(raw, normalizeSummary))
    } catch (cause) {
      onNotice(errorText(cause))
    }
  }

  const toggleSelected = (id: string) => {
    setSelected((current) => {
      const next = new Set(current)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  return (
    <div className="page-stack">
      <section className="hero-card compact">
        <div>
          <p className="eyebrow">Admin</p>
          <h2>삭제된 게시글 관리</h2>
          <p className="hero-copy">삭제된 글을 확인하고, 필요한 항목만 골라 복구할 수 있습니다.</p>
        </div>
        <div className="hero-actions">
          <button className="primary-button" type="button" onClick={restore}>
            복구 실행
          </button>
        </div>
      </section>

      <section className="content-grid">
        {loading ? (
          <div className="panel loading-panel">삭제된 게시글을 불러오는 중...</div>
        ) : data.items.length ? (
          data.items.map((item) => {
            const checked = selected.has(item.id)
            return (
              <article
                className="content-card list-row-card deleted"
                key={item.id}
                role="button"
                tabIndex={0}
                onClick={() => toggleSelected(item.id)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault()
                    toggleSelected(item.id)
                  }
                }}
              >
                <div className="list-row-main">
                  <label className="deleted-row-check">
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => toggleSelected(item.id)}
                      onClick={(event) => event.stopPropagation()}
                    />
                    <span>선택</span>
                  </label>

                  <div className="list-row-text">
                    <h3 className="list-title">{item.title}</h3>
                    <p className="list-summary">{item.description || '삭제된 게시글입니다.'}</p>
                  </div>

                  <div className="list-row-right">
                    <span className="list-id-label">ID</span>
                    <strong className="list-id">{item.id}</strong>
                  </div>
                </div>

                <div className="list-row-meta">
                  <span className="chip">{item.createdBy || 'unknown'}</span>
                  <span className="chip subtle">{dateText(item.createdDate)}</span>
                </div>
              </article>
            )
          })
        ) : (
          <EmptyState
            title="삭제된 게시글이 없습니다"
            description="복구할 항목이 아직 없습니다."
            actions={
              <Link className="primary-button" to="/">
                전체 게시판으로 이동
              </Link>
            }
          />
        )}
      </section>

      <Pagination
        page={data.page}
        totalPages={data.totalPages}
        onPageChange={(page) => setQuery((current) => ({ ...current, page }))}
      />
    </div>
  )
}
