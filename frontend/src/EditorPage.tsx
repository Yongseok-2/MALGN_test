import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createContent, getContent, updateContent } from './api'
import type { ContentDraft, Session } from './types'
import { bytesText, errorText, normalizeDetail, text, toRecord } from './utils'

type Props = {
  session: Session | null
  mode: 'create' | 'edit'
  onNotice: (message: string) => void
}

export function EditorPage({ session, mode, onNotice }: Props) {
  const navigate = useNavigate()
  const { id = '' } = useParams()
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [files, setFiles] = useState<File[]>([])
  const [loading, setLoading] = useState(mode === 'edit')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let active = true
    if (mode !== 'edit' || !id) return
    ;(async () => {
      setLoading(true)
      try {
        const raw = await getContent(id, { page: 0, size: 10, sort: 'createdDate,desc' })
        if (!active) return
        const content = normalizeDetail(raw)
        setTitle(content.title || '')
        setDescription(content.description || '')
      } catch (cause) {
        if (active) onNotice(errorText(cause))
      } finally {
        if (active) setLoading(false)
      }
    })()
    return () => {
      active = false
    }
  }, [id, mode, onNotice])

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSaving(true)
    try {
      if (mode === 'edit') {
        await updateContent(id, { title, description } as ContentDraft)
        onNotice('게시글을 수정했습니다.')
        navigate(`/content/${id}`)
      } else {
        const result = await createContent({ title, description, files })
        const record = toRecord(result)
        const nextId = text(record.id) || text(record.contentId)
        onNotice('게시글을 작성했습니다.')
        navigate(nextId ? `/content/${nextId}` : '/')
      }
    } catch (cause) {
      onNotice(errorText(cause))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="page-stack">
      <section className="hero-card compact">
        <div>
          <p className="eyebrow">Editor</p>
          <h2>{mode === 'edit' ? '게시글 수정' : '새 글 작성'}</h2>
          <p className="hero-copy">
            {mode === 'edit'
              ? '제목과 설명을 다시 저장합니다.'
              : '글을 작성하고 파일을 첨부할 수 있습니다.'}
          </p>
        </div>
        <div className="hero-actions">
          <Link className="ghost-button inline-link" to="/">
            목록으로
          </Link>
          <p className="mini-copy">
            {session ? `현재 사용자: ${session.username}` : '로그인 후 작성하는 흐름이 일반적입니다.'}
          </p>
        </div>
      </section>

      <section className="panel form-panel">
        {loading ? (
          <div className="loading-panel">게시글 정보를 불러오는 중...</div>
        ) : (
          <form className="editor-form" onSubmit={submit}>
            <label>
              <span>제목</span>
              <input
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                placeholder="제목을 입력하세요"
                required
              />
            </label>
            <label>
              <span>내용</span>
              <textarea
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder="내용을 입력하세요"
                rows={10}
                required
              />
            </label>
            {mode === 'create' ? (
              <label>
                <span>첨부 파일</span>
                <input
                  type="file"
                  multiple
                  accept="*/*"
                  onChange={(event) => setFiles(Array.from(event.target.files || []))}
                />
              </label>
            ) : null}
            {mode === 'create' && files.length ? (
              <div className="attachment-list">
                {files.map((file) => (
                  <span className="chip subtle" key={`${file.name}-${file.size}`}>
                    {file.name} {bytesText(file.size)}
                  </span>
                ))}
              </div>
            ) : null}
            <div className="card-actions">
              <button className="primary-button" type="submit" disabled={saving}>
                {saving ? '저장 중...' : mode === 'edit' ? '수정 저장' : '작성 완료'}
              </button>
              <Link className="ghost-button" to="/">
                취소
              </Link>
            </div>
          </form>
        )}
      </section>
    </div>
  )
}
