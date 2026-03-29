import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
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

export function DeletedPage({ session, onNotice }: Props) {
  const navigate = useNavigate()
  const [draft, setDraft] = useState(DEFAULT_QUERY)
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

  const apply = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setQuery({ ...draft, page: 0 })
  }

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

  return (
    <div className="page-stack">
      <section className="hero-card compact">
        <div>
          <p className="eyebrow">Admin</p>
          <h2>삭제된 게시글 관리</h2>
          <p className="hero-copy">soft delete 된 글을 검색하고, 선택해서 복구할 수 있습니다.</p>
        </div>
        <div className="hero-actions">
          <button className="primary-button" type="button" onClick={restore}>
            복구 실행
          </button>
          <p className="mini-copy">
            {session?.role === 'ADMIN'
              ? 'ADMIN 계정으로 보이는 상태입니다.'
              : '권한이 부족하면 서버에서 403이 발생할 수 있습니다.'}
          </p>
        </div>
      </section>

      <section className="panel">
        <form className="search-bar" onSubmit={apply}>
          <label className="wide">
            <span>키워드</span>
            <input
              value={draft.keyword || ''}
              onChange={(event) => setDraft((current) => ({ ...current, keyword: event.target.value }))}
              placeholder="삭제된 글 검색"
            />
          </label>
          <button className="primary-button" type="submit">
            검색
          </button>
        </form>
      </section>

      <section className="content-grid">
        {loading ? (
          <div className="panel loading-panel">삭제된 게시글을 불러오는 중...</div>
        ) : data.items.length ? (
          data.items.map((item) => {
            const checked = selected.has(item.id)
            return (
              <article className="content-card deleted" key={item.id}>
                <label className="checkbox-row">
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={(event) =>
                      setSelected((current) => {
                        const next = new Set(current)
                        if (event.target.checked) next.add(item.id)
                        else next.delete(item.id)
                        return next
                      })
                    }
                  />
                  <span>선택</span>
                </label>
                <h3>{item.title}</h3>
                <p>{item.description || '삭제된 게시글입니다.'}</p>
                <div className="card-meta">
                  <span>{item.createdBy || 'unknown'}</span>
                  <span>{dateText(item.createdDate)}</span>
                </div>
                <div className="card-actions">
                  <button className="ghost-button" type="button" onClick={() => navigate(`/content/${item.id}`)}>
                    상세
                  </button>
                  <button className="ghost-button" type="button" onClick={() => setSelected(new Set([item.id]))}>
                    단일 선택
                  </button>
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
                게시판으로 이동
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
