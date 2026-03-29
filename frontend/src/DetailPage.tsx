import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createComment, deleteComment, deleteContent, getContent, updateComment } from './api'
import type { CommentItem, ContentDetail, Session } from './types'
import { bytesText, dateText, errorText, normalizeDetail } from './utils'
import { EmptyState } from './ui'

type Props = {
  session: Session | null
  onNotice: (message: string) => void
}

function canEdit(session: Session | null, owner?: string) {
  if (!session || !owner) return false
  return session.role === 'ADMIN' || session.username === owner
}

function handleDownload(storeFileName?: string) {
  if (!storeFileName) return
  window.location.href = `http://localhost:8888/api/content/download/${storeFileName}`
}

export function DetailPage({ session, onNotice }: Props) {
  const navigate = useNavigate()
  const { id = '' } = useParams()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [detail, setDetail] = useState<ContentDetail | null>(null)
  const [commentText, setCommentText] = useState('')
  const [editingComment, setEditingComment] = useState<CommentItem | null>(null)
  const [commentDraft, setCommentDraft] = useState('')

  const ownerAllowed = useMemo(() => canEdit(session, detail?.createdBy), [session, detail?.createdBy])

  const goEdit = () => {
    if (!ownerAllowed) {
      window.alert('본인이 작성한 글만 수정할 수 있습니다.')
      return
    }
    navigate(`/edit/${detail?.id}`)
  }

  const load = async () => {
    if (!id) return
    setLoading(true)
    try {
      setDetail(normalizeDetail(await getContent(id, { page: 0, size: 50, sort: 'createdDate,asc' })))
    } catch (cause) {
      setDetail(null)
      onNotice(errorText(cause))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  const submitComment = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!commentText.trim()) return
    setSaving(true)
    try {
      await createComment(id, commentText.trim())
      setCommentText('')
      onNotice('댓글을 작성했습니다.')
      await load()
    } catch (cause) {
      onNotice(errorText(cause))
    } finally {
      setSaving(false)
    }
  }

  const removeContent = async () => {
    if (!ownerAllowed) {
      window.alert('본인이 작성한 글만 수정하거나 삭제할 수 있습니다.')
      return
    }
    if (!window.confirm('게시글을 삭제할까요?')) return
    try {
      await deleteContent(id)
      onNotice('게시글을 삭제했습니다.')
      navigate('/')
    } catch (cause) {
      onNotice(errorText(cause))
    }
  }

  const beginEdit = (comment: CommentItem) => {
    setEditingComment(comment)
    setCommentDraft(comment.text)
  }

  const saveComment = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!editingComment) return
    try {
      await updateComment(editingComment.id, commentDraft.trim())
      onNotice('댓글을 수정했습니다.')
      setEditingComment(null)
      setCommentDraft('')
      await load()
    } catch (cause) {
      onNotice(errorText(cause))
    }
  }

  const removeComment = async (commentId: string) => {
    if (!window.confirm('댓글을 삭제할까요?')) return
    try {
      await deleteComment(commentId)
      onNotice('댓글을 삭제했습니다.')
      await load()
    } catch (cause) {
      onNotice(errorText(cause))
    }
  }

  if (loading) return <div className="panel loading-panel">게시글을 불러오는 중...</div>
  if (!detail) {
    return (
      <EmptyState
        title="게시글을 찾지 못했습니다"
        description="목록으로 돌아가 다시 선택해 주세요."
        actions={
          <Link className="primary-button" to="/">
            목록으로
          </Link>
        }
      />
    )
  }

  return (
    <div className="page-stack detail-page">
      <section className="detail-top">
        <div className="detail-title-row">
          <h2>{detail.title}</h2>
          <div className="detail-id">#{detail.id}</div>
        </div>
        <div className="detail-meta-row">
          <span>작성자 {detail.createdBy || '-'}</span>
          <span>조회수 {detail.viewCount ?? 0}</span>
          <span>작성일 {dateText(detail.createdDate)}</span>
        </div>
        <div className="card-actions detail-actions">
          <button className="ghost-button" type="button" onClick={goEdit}>
            수정
          </button>
          <button className="ghost-button" type="button" onClick={removeContent}>
            삭제
          </button>
        </div>
      </section>

      <section className="detail-stack">
        <div className="detail-block">
          <h3>본문</h3>
          <p className="detail-body">{detail.description || '본문이 비어 있습니다.'}</p>
        </div>

        <div className="detail-block">
          <h3>첨부파일</h3>
          {detail.attachments?.length ? (
            <ul className="attachment-grid">
              {detail.attachments.map((file, index) => (
                <li key={`${file.id || file.name || index}`}>
                  <button
                    type="button"
                    className="attachment-download"
                    onClick={() => handleDownload(file.storeFileName || file.path || undefined)}
                  >
                    {file.originalFileName || file.name || file.path || file.id || '첨부파일'}
                  </button>
                  {file.size ? <small>{bytesText(file.size)}</small> : null}
                </li>
              ))}
            </ul>
          ) : (
            <p className="mini-copy">첨부 파일이 없습니다.</p>
          )}
        </div>

        <div className="detail-block">
          <h3>댓글</h3>
          <form className="comment-form" onSubmit={submitComment}>
            <textarea
              value={commentText}
              onChange={(event) => setCommentText(event.target.value)}
              rows={4}
              placeholder="댓글을 입력하세요"
            />
            <button className="primary-button" type="submit" disabled={saving}>
              {saving ? '전송 중...' : '댓글 작성'}
            </button>
          </form>

          <div className="comment-list">
            {detail.comments?.length ? (
              detail.comments.map((comment) => {
                const isEditing = editingComment?.id === comment.id
                const allowed = canEdit(session, comment.createdBy)
                return (
                  <article className="comment-card" key={comment.id}>
                    <div className="comment-head">
                      <strong>{comment.createdBy || 'anonymous'}</strong>
                      <span>{dateText(comment.createdDate)}</span>
                    </div>
                    {isEditing ? (
                      <form className="comment-edit" onSubmit={saveComment}>
                        <textarea
                          value={commentDraft}
                          onChange={(event) => setCommentDraft(event.target.value)}
                          rows={4}
                        />
                        <div className="card-actions">
                          <button className="primary-button" type="submit">
                            저장
                          </button>
                          <button
                            className="ghost-button"
                            type="button"
                            onClick={() => setEditingComment(null)}
                          >
                            취소
                          </button>
                        </div>
                      </form>
                    ) : (
                      <>
                        <p>{comment.text}</p>
                        <div className="card-actions">
                          <button
                            className="ghost-button"
                            type="button"
                            onClick={() => {
                              if (!allowed) {
                                window.alert('본인이 작성한 댓글만 수정할 수 있습니다.')
                                return
                              }
                              beginEdit(comment)
                            }}
                          >
                            수정
                          </button>
                          <button
                            className="ghost-button"
                            type="button"
                            onClick={() => {
                              if (!allowed) {
                                window.alert('본인이 작성한 댓글만 삭제할 수 있습니다.')
                                return
                              }
                              void removeComment(comment.id)
                            }}
                          >
                            삭제
                          </button>
                        </div>
                      </>
                    )}
                  </article>
                )
              })
            ) : (
              <p className="mini-copy">아직 댓글이 없습니다.</p>
            )}
          </div>
        </div>
      </section>

      <div className="card-actions">
        <Link className="ghost-button" to="/">
          목록으로
        </Link>
        <Link className="primary-button" to="/new">
          새 글 작성
        </Link>
      </div>
    </div>
  )
}
