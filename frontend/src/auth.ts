export function normalizeRole(value?: string) {
  const role = value?.trim().toUpperCase() || ''
  if (role === 'ROLE_ADMIN') return 'ADMIN'
  if (role === 'ROLE_USER') return 'USER'
  return role
}

