export type Session = {
  username: string
  role?: string
  loggedInAt: string
}

export type ContentQuery = {
  page: number
  size: number
  sort: string
  type?: string
  keyword?: string
}

export type PageData<T> = {
  items: T[]
  page: number
  size: number
  totalPages: number
  totalElements: number
}

export type Attachment = {
  id?: string
  name?: string
  originalFileName?: string
  storeFileName?: string
  url?: string
  path?: string
  size?: number
  mimeType?: string
}

export type CommentItem = {
  id: string
  text: string
  createdBy?: string
  createdDate?: string
  lastModifiedDate?: string
  deleted?: boolean
}

export type ContentSummary = {
  id: string
  title: string
  description?: string
  createdBy?: string
  createdDate?: string
  lastModifiedDate?: string
  viewCount?: number
  deleted?: boolean
  attachments?: Attachment[]
}

export type ContentDetail = ContentSummary & {
  comments?: CommentItem[]
}

export type ContentDraft = {
  title: string
  description: string
}

export type ContentCreateInput = ContentDraft & {
  files?: File[]
}

export type AuthInput = {
  username: string
  password: string
}
