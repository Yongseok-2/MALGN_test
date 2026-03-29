import { useState } from 'react'
import type { FormEvent } from 'react'
import { login, logout, signup } from './api'
import type { Session } from './types'
import { dateText, errorText, text, toRecord } from './utils'

type Props = {
  session: Session | null
  setSession: (value: Session | null) => void
  onLoginDone: () => void
  onLogoutDone: () => void
  onNotice: (message: string) => void
}

export function AuthPanel({ session, setSession, onLoginDone, onLogoutDone, onNotice }: Props) {
  const [mode, setMode] = useState<'login' | 'signup'>('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    try {
      const data =
        mode === 'signup'
          ? await (async () => {
              await signup({ username, password })
              return login({ username, password })
            })()
          : await login({ username, password })

      const record = toRecord(await data)
      setSession({
        username: text(record.username) || text(record.userName) || text(record.name) || username,
        role: text(record.role) || session?.role,
        loggedInAt: new Date().toISOString(),
      })
      setPassword('')
      onNotice(mode === 'login' ? '로그인했습니다.' : '회원가입 후 로그인했습니다.')
      onLoginDone()
    } catch (error) {
      onNotice(errorText(error))
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = async () => {
    try {
      await logout()
    } catch {
      // ignore
    } finally {
      setSession(null)
      onLogoutDone()
      onNotice('로그아웃했습니다.')
    }
  }

  return (
    <section className="panel auth-panel">
      <div className="panel-head">
        <div>
          <p className="eyebrow">Auth</p>
          <h2>{session ? '세션 상태' : '로그인 / 회원가입'}</h2>
        </div>
        {session ? (
          <button className="ghost-button" type="button" onClick={handleLogout}>
            로그아웃
          </button>
        ) : null}
      </div>

      {session ? (
        <div className="session-summary">
          <div>
            <p className="mini-title">{session.username}</p>
            <p className="mini-copy">{session.role || '권한 미확인'}</p>
          </div>
          <p className="mini-copy">로그인 시각 {dateText(session.loggedInAt)}</p>
        </div>
      ) : (
        <>
          <div className="segmented-control">
            <button
              type="button"
              className={`segmented${mode === 'login' ? ' active' : ''}`}
              onClick={() => setMode('login')}
            >
              로그인
            </button>
            <button
              type="button"
              className={`segmented${mode === 'signup' ? ' active' : ''}`}
              onClick={() => setMode('signup')}
            >
              회원가입
            </button>
          </div>

          <form className="auth-form" onSubmit={submit}>
            <label>
              <span>아이디</span>
              <input
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                placeholder="user1"
                autoComplete="username"
              />
            </label>
            <label>
              <span>비밀번호</span>
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="password123"
                autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
              />
            </label>
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? '처리 중...' : mode === 'login' ? '로그인' : '회원가입'}
            </button>
          </form>
        </>
      )}
    </section>
  )
}
