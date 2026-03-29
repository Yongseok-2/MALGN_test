import { useEffect, useState } from 'react'
import {
  HashRouter,
  Link,
  NavLink,
  Route,
  Routes,
  useNavigate,
} from 'react-router-dom'
import { AuthPanel } from './AuthPanel'
import { DeletedPage } from './DeletedPage'
import { DetailPage } from './DetailPage'
import { EditorPage } from './EditorPage'
import { ListPage } from './ListPage'
import { listContents, listMyContents, logout } from './api'
import type { Session } from './types'
import './App.css'

const STORAGE_KEY = 'malgn.session'

function useSession() {
  const [session, setSession] = useState<Session | null>(() => {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (!saved) return null
    try {
      return JSON.parse(saved) as Session
    } catch {
      return null
    }
  })

  useEffect(() => {
    if (session) localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    else localStorage.removeItem(STORAGE_KEY)
  }, [session])

  return { session, setSession }
}

function NoticeAutoClear({
  notice,
  setNotice,
}: {
  notice: string
  setNotice: (value: string) => void
}) {
  useEffect(() => {
    if (!notice) return
    const timer = window.setTimeout(() => setNotice(''), 2500)
    return () => window.clearTimeout(timer)
  }, [notice, setNotice])

  return null
}

function AppShell({
  session,
  setSession,
  notice,
  setNotice,
}: {
  session: Session
  setSession: (value: Session | null) => void
  notice: string
  setNotice: (value: string) => void
}) {
  const navigate = useNavigate()

  const nav = [
    ['/', '전체 게시판'],
    ['/new', '글 작성'],
    ['/me', '내 글'],
  ].concat(session.role === 'ADMIN' ? [['/admin', '관리자']] : [])

  const handleLogout = async () => {
    try {
      await logout()
    } catch {
      // ignore
    } finally {
      setSession(null)
      setNotice('로그아웃했습니다.')
      navigate('/')
    }
  }

  return (
    <div className="app-shell">
      <div className="ambient ambient-a" />
      <div className="ambient ambient-b" />

      <header className="topbar">
        <Link className="brand-block brand-link brand-text" to="/">
          <h1>MALGN CMS</h1>
        </Link>

        <nav className="topnav" aria-label="Primary">
          {nav.map(([to, label]) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `topnav-link${isActive ? ' active' : ''}`}
            >
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="session-pill">
          <span className={`session-dot ${session ? 'online' : 'offline'}`} />
          <strong>{session.username}</strong>
          <button className="ghost-button pill-button" type="button" onClick={handleLogout}>
            로그아웃
          </button>
        </div>
      </header>

      {notice ? <div className="notice-banner">{notice}</div> : null}

      <main className="workspace">
        <section className="main-stage">
          <Routes>
            <Route
              path="/"
              element={
                <ListPage
                  title="전체 게시판"
                  subtitle="게시글을 빠르게 훑고, 카드 전체를 눌러 상세로 들어갈 수 있습니다."
                  fetcher={listContents}
                />
              }
            />
            <Route
              path="/me"
              element={
                <ListPage
                  title="내가 작성한 글"
                  subtitle="내가 쓴 글만 모아서 봅니다."
                  fetcher={listMyContents}
                  hideTypeFilter
                  disableSearch
                />
              }
            />
            <Route path="/admin" element={<DeletedPage session={session} onNotice={setNotice} />} />
            <Route path="/new" element={<EditorPage session={session} mode="create" onNotice={setNotice} />} />
            <Route path="/edit/:id" element={<EditorPage session={session} mode="edit" onNotice={setNotice} />} />
            <Route path="/content/:id" element={<DetailPage session={session} onNotice={setNotice} />} />
            <Route
              path="*"
              element={
                <section className="panel empty-state">
                  <h3>페이지를 찾지 못했습니다</h3>
                  <p>왼쪽 메뉴에서 원하는 화면으로 이동해 주세요.</p>
                </section>
              }
            />
          </Routes>
        </section>
      </main>
    </div>
  )
}

function LoginGate({
  session,
  setSession,
  setNotice,
}: {
  session: Session | null
  setSession: (value: Session | null) => void
  setNotice: (value: string) => void
}) {
  const navigate = useNavigate()

  if (session) return null

  return (
    <div className="app-shell auth-gate">
      <div className="ambient ambient-a" />
      <div className="ambient ambient-b" />
      <section className="auth-gate-card">
        <Link className="brand-block brand-link brand-text" to="/">
          <h1>MALGN CMS</h1>
        </Link>
        <p className="hero-copy">로그인해야 게시판, 댓글, 내 글, 관리자 기능을 볼 수 있습니다.</p>
        <AuthPanel
          session={session}
          setSession={setSession}
          onLoginDone={() => navigate('/')}
          onLogoutDone={() => {}}
          onNotice={setNotice}
        />
      </section>
    </div>
  )
}

function AppContentWithAutoClear() {
  const { session, setSession } = useSession()
  const [notice, setNotice] = useState('')

  return (
    <>
      <NoticeAutoClear notice={notice} setNotice={setNotice} />
      {session ? (
        <AppShell session={session} setSession={setSession} notice={notice} setNotice={setNotice} />
      ) : (
        <LoginGate session={session} setSession={setSession} setNotice={setNotice} />
      )}
    </>
  )
}

export default function App() {
  return (
    <HashRouter>
      <AppContentWithAutoClear />
    </HashRouter>
  )
}
