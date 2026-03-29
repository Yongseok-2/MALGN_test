import axios from 'axios'
import type {
  AuthInput,
  ContentCreateInput,
  ContentDraft,
  ContentQuery,
} from './types'

export const api = axios.create({
  baseURL: 'http://localhost:8888',
  withCredentials: true,
})

function toParams(query: Partial<ContentQuery>) {
  const params: Record<string, string | number> = {}
  if (query.page !== undefined) params.page = query.page
  if (query.size !== undefined) params.size = query.size
  if (query.sort) params.sort = query.sort
  if (query.type) params.type = query.type
  if (query.keyword) params.keyword = query.keyword
  return params
}

export async function signup(payload: AuthInput) {
  return (await api.post('/api/auth/signup', payload)).data
}

export async function login(payload: AuthInput) {
  return (await api.post('/api/auth/login', payload)).data
}

export async function logout() {
  return (await api.post('/api/auth/logout')).data
}

export async function getCurrentUser() {
  return (await api.get('/api/auth/me')).data
}

export async function listContents(query: ContentQuery) {
  return (await api.get('/api/content', { params: toParams(query) })).data
}

export async function getContent(
  id: string,
  query: Pick<ContentQuery, 'page' | 'size' | 'sort'>,
) {
  return (await api.get(`/api/content/${id}`, { params: toParams(query) })).data
}

export async function createContent(payload: ContentCreateInput) {
  const formData = new FormData()
  formData.append(
    'content',
    new Blob([JSON.stringify({ title: payload.title, description: payload.description })], {
      type: 'application/json',
    }),
  )
  for (const file of payload.files || []) {
    formData.append('files', file)
  }
  return (await api.post('/api/content/add', formData)).data
}

export async function updateContent(id: string, payload: ContentDraft) {
  return (await api.put(`/api/content/${id}`, payload)).data
}

export async function deleteContent(id: string) {
  return (await api.delete(`/api/content/${id}`)).data
}

export async function createComment(contentId: string, text: string) {
  return (await api.post(`/api/content/${contentId}/comments`, { text })).data
}

export async function updateComment(commentId: string, text: string) {
  return (await api.patch(`/api/comments/${commentId}`, { text })).data
}

export async function deleteComment(commentId: string) {
  return (await api.delete(`/api/comments/${commentId}`)).data
}

export async function listMyContents(query: ContentQuery) {
  return (await api.get('/api/contents/me', { params: toParams(query) })).data
}

export async function listDeletedContents(query: ContentQuery) {
  return (await api.get('/api/admin/deletedContents', { params: toParams(query) })).data
}

export async function restoreContents(ids: Array<string | number>) {
  return (await api.patch('/api/admin/restoreContents', ids)).data
}
