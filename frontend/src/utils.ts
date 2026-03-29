import type { ContentDetail, ContentSummary, PageData } from './types'

export function toRecord(value: unknown) {
  return value && typeof value === 'object' && !Array.isArray(value) ? (value as Record<string, unknown>) : {}
}

export function text(value: unknown, fallback = '') {
  return typeof value === 'string' ? value : typeof value === 'number' ? String(value) : fallback
}

export function num(value: unknown, fallback = 0) {
  if (typeof value === 'number' && Number.isFinite(value)) return value
  if (typeof value === 'string' && value.trim()) {
    const parsed = Number(value)
    if (Number.isFinite(parsed)) return parsed
  }
  return fallback
}

export function arr(value: unknown) {
  return Array.isArray(value) ? value : []
}

export function listFrom(value: unknown) {
  if (Array.isArray(value)) return value
  const record = toRecord(value)
  if (Array.isArray(record.content)) return record.content
  if (Array.isArray(record.data)) return record.data
  if (Array.isArray(record.items)) return record.items
  if (Array.isArray(record.list)) return record.list
  return []
}

export function normalizePage<T>(raw: unknown, mapper: (item: unknown) => T): PageData<T> {
  const record = toRecord(raw)
  const items = listFrom(record)
  const size = num(record.size, items.length || 10)
  const totalElements = num(record.totalElements, items.length)
  return {
    items: items.map(mapper),
    page: num(record.number, num(record.page, 0)),
    size,
    totalPages: num(record.totalPages, size ? Math.max(1, Math.ceil(totalElements / size)) : 1),
    totalElements,
  }
}

export function normalizeSummary(raw: unknown): ContentSummary {
  const record = toRecord(raw)
  const files = [...arr(record.files), ...arr(record.attachments), ...arr(record.images)]
  return {
    id: text(record.id),
    title: text(record.title),
    description: text(record.description),
    createdBy: text(record.createdBy) || text(record.author),
    createdDate: text(record.createdDate) || text(record.createdAt),
    lastModifiedDate: text(record.lastModifiedDate) || text(record.updatedAt),
    viewCount: num(record.viewCount) || undefined,
    deleted: Boolean(record.deleted),
    attachments: files.map((item) => {
      const file = toRecord(item)
      const originalName =
        text(file.originalFileName) ||
        text(file.originalFilename) ||
        text(file.original_file_name) ||
        text(file.originalname)
      const storedName =
        text(file.storeFileName) || text(file.storeFilename) || text(file.store_file_name)
      return {
        id: text(file.id),
        originalFileName: originalName || undefined,
        storeFileName: storedName || undefined,
        name:
          originalName ||
          text(file.name) ||
          text(file.originalName) ||
          text(file.fileName) ||
          text(file.filename) ||
          storedName,
        url: text(file.url) || text(file.fileUrl) || text(file.downloadUrl) || text(file.filePath),
        path: text(file.path) || text(file.filePath),
        size: num(file.size) || num(file.fileSize) || undefined,
        mimeType: text(file.mimeType) || text(file.contentType),
      }
    }),
  }
}

export function normalizeDetail(raw: unknown): ContentDetail {
  const record = toRecord(raw)
  const base = normalizeSummary(record)
  return {
    ...base,
    comments: listFrom(record.comments).map((item) => {
      const c = toRecord(item)
      return {
        id: text(c.id),
        text: text(c.text) || text(c.content),
        createdBy: text(c.createdBy) || text(c.writer),
        createdDate: text(c.createdDate) || text(c.createdAt),
        lastModifiedDate: text(c.lastModifiedDate) || text(c.updatedAt),
        deleted: Boolean(c.deleted),
      }
    }),
  }
}

export function dateText(value?: string) {
  if (!value) return '-'
  const date = new Date(value)
  return Number.isNaN(date.getTime())
    ? value
    : new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      }).format(date)
}

export function bytesText(bytes?: number) {
  if (!bytes) return ''
  return bytes < 1024 ? `${bytes} B` : bytes < 1024 * 1024 ? `${(bytes / 1024).toFixed(1)} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

export function errorText(error: unknown) {
  const record = toRecord(error)
  const response = toRecord(record.response)
  const data = toRecord(response.data)
  return text(data.message) || text(data.detail) || text(data.error) || text(record.message) || '요청을 처리하지 못했습니다.'
}
