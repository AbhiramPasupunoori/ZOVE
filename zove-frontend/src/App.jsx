import { useEffect, useState } from 'react'
import axios from 'axios'
import {
  Bell,
  Camera,
  CheckCircle2,
  Heart,
  Home,
  ImagePlus,
  Loader2,
  Lock,
  LogIn,
  LogOut,
  MessageCircle,
  RefreshCw,
  Search,
  Send,
  ShieldCheck,
  UserPlus,
  Users,
} from 'lucide-react'
import heroImg from './assets/hero.png'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'
const AUTH_STORAGE_KEY = 'zove.auth'

const api = axios.create({
  baseURL: API_BASE_URL,
})

const roadmapItems = [
  ['Project setup', 'done'],
  ['JWT auth', 'done'],
  ['User profiles', 'next'],
  ['Posts', 'queued'],
  ['Likes and comments', 'queued'],
  ['Follow graph', 'queued'],
  ['Home feed', 'queued'],
  ['Notifications', 'queued'],
  ['Messaging', 'queued'],
]

const feedItems = [
  {
    author: 'Mira Chen',
    handle: '@mira',
    time: '12m',
    text: 'Profile foundations are next. Bio, avatar, cover image, and editable public identity are lined up after auth.',
    accent: 'teal',
    stats: ['128', '24'],
  },
  {
    author: 'Rohan Mehta',
    handle: '@rohan',
    time: '31m',
    text: 'JWT tokens are ready for protected routes. Posts can now belong to a real user instead of a temporary demo account.',
    accent: 'rose',
    stats: ['89', '13'],
  },
  {
    author: 'ZOVE System',
    handle: '@zove',
    time: '1h',
    text: 'The backend health endpoint, MySQL configuration, and H2 test database are all part of the base layer.',
    accent: 'gold',
    stats: ['204', '42'],
  },
]

const navItems = [
  [Home, 'Home'],
  [Search, 'Search'],
  [Bell, 'Notifications'],
  [MessageCircle, 'Messages'],
  [Users, 'People'],
]

function readStoredAuth() {
  try {
    return JSON.parse(localStorage.getItem(AUTH_STORAGE_KEY))
  } catch {
    return null
  }
}

function App() {
  const [health, setHealth] = useState({
    state: 'checking',
    message: 'Checking backend',
  })
  const [authMode, setAuthMode] = useState('register')
  const [authForm, setAuthForm] = useState({
    displayName: '',
    username: '',
    email: '',
    password: '',
  })
  const [auth, setAuth] = useState(readStoredAuth)
  const [authStatus, setAuthStatus] = useState({ state: 'idle', message: '' })

  const currentUser = auth?.user

  async function checkHealth() {
    setHealth({ state: 'checking', message: 'Checking backend' })
    try {
      const { data } = await api.get('/health')
      setHealth({
        state: 'online',
        message: data.message ?? 'Backend online',
        timestamp: data.timestamp,
      })
    } catch {
      setHealth({
        state: 'offline',
        message: 'Backend offline',
      })
    }
  }

  useEffect(() => {
    let ignore = false

    api
      .get('/health')
      .then(({ data }) => {
        if (!ignore) {
          setHealth({
            state: 'online',
            message: data.message ?? 'Backend online',
            timestamp: data.timestamp,
          })
        }
      })
      .catch(() => {
        if (!ignore) {
          setHealth({
            state: 'offline',
            message: 'Backend offline',
          })
        }
      })

    return () => {
      ignore = true
    }
  }, [])

  useEffect(() => {
    if (!auth?.token) {
      return
    }

    let ignore = false

    api
      .get('/auth/me', { headers: { Authorization: `Bearer ${auth.token}` } })
      .then(({ data }) => {
        if (!ignore) {
          setAuth((storedAuth) => (storedAuth ? { ...storedAuth, user: data } : storedAuth))
        }
      })
      .catch(() => {
        if (!ignore) {
          localStorage.removeItem(AUTH_STORAGE_KEY)
          setAuth(null)
        }
      })

    return () => {
      ignore = true
    }
  }, [auth?.token])

  function updateAuthForm(event) {
    const { name, value } = event.target
    setAuthForm((form) => ({ ...form, [name]: value }))
  }

  async function submitAuth(event) {
    event.preventDefault()
    setAuthStatus({ state: 'loading', message: '' })

    const payload =
      authMode === 'register'
        ? authForm
        : {
            email: authForm.email,
            password: authForm.password,
          }

    try {
      const { data } = await api.post(`/auth/${authMode}`, payload)
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(data))
      setAuth(data)
      setAuthStatus({
        state: 'success',
        message: authMode === 'register' ? 'Account created' : 'Signed in',
      })
    } catch (error) {
      const message =
        error.response?.data?.detail ??
        error.response?.data?.message ??
        error.response?.data?.error ??
        'Request failed'
      setAuthStatus({ state: 'error', message })
    }
  }

  function logout() {
    localStorage.removeItem(AUTH_STORAGE_KEY)
    setAuth(null)
    setAuthStatus({ state: 'idle', message: '' })
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand" aria-label="ZOVE">
          <span className="brand-mark">Z</span>
          <span className="brand-name">ZOVE</span>
        </div>

        <nav className="nav" aria-label="Primary navigation">
          {navItems.map(([Icon, label], index) => (
            <button
              className={`icon-button ${index === 0 ? 'active' : ''}`}
              type="button"
              aria-label={label}
              title={label}
              key={label}
            >
              <Icon size={20} />
            </button>
          ))}
        </nav>

        <button
          className={`health-chip ${health.state}`}
          type="button"
          onClick={checkHealth}
          title="Refresh backend status"
        >
          {health.state === 'checking' ? <Loader2 size={16} /> : <ShieldCheck size={16} />}
          <span>{health.state === 'online' ? 'API' : health.state}</span>
        </button>
      </aside>

      <main className="feed-column">
        <header className="feed-header">
          <div>
            <p className="eyebrow">Social workspace</p>
            <h1>ZOVE</h1>
          </div>
          <button className="text-button" type="button" onClick={checkHealth}>
            <RefreshCw size={18} />
            Sync
          </button>
        </header>

        <section className="composer" aria-label="Create post">
          <div className="avatar">
            {currentUser ? currentUser.displayName.charAt(0).toUpperCase() : 'Z'}
          </div>
          <div className="composer-body">
            <textarea
              rows="3"
              placeholder={currentUser ? 'Share an update' : 'Sign in to post'}
              disabled={!currentUser}
            />
            <div className="composer-actions">
              <div className="tool-row">
                <button className="icon-button small" type="button" aria-label="Add image" title="Add image">
                  <ImagePlus size={18} />
                </button>
                <button className="icon-button small" type="button" aria-label="Open camera" title="Open camera">
                  <Camera size={18} />
                </button>
              </div>
              <button className="post-button" type="button" disabled={!currentUser}>
                <Send size={17} />
                Post
              </button>
            </div>
          </div>
        </section>

        <section className="feed-list" aria-label="Home feed">
          {feedItems.map((item) => (
            <article className={`post-card ${item.accent}`} key={item.author}>
              <div className="post-top">
                <div className="avatar compact">{item.author.charAt(0)}</div>
                <div>
                  <h2>{item.author}</h2>
                  <p>{item.handle} · {item.time}</p>
                </div>
              </div>
              <p className="post-text">{item.text}</p>
              <div className="post-actions">
                <button type="button">
                  <Heart size={17} />
                  {item.stats[0]}
                </button>
                <button type="button">
                  <MessageCircle size={17} />
                  {item.stats[1]}
                </button>
              </div>
            </article>
          ))}
        </section>
      </main>

      <aside className="side-panel">
        <section className="account-panel">
          <div className="panel-heading">
            <Lock size={19} />
            <h2>{currentUser ? 'Account' : 'Authentication'}</h2>
          </div>

          {currentUser ? (
            <div className="signed-in">
              <div className="profile-strip">
                <img src={heroImg} alt="" />
                <div className="avatar profile-avatar">
                  {currentUser.displayName.charAt(0).toUpperCase()}
                </div>
              </div>
              <h3>{currentUser.displayName}</h3>
              <p>@{currentUser.username}</p>
              <p>{currentUser.email}</p>
              <button className="text-button full" type="button" onClick={logout}>
                <LogOut size={18} />
                Sign out
              </button>
            </div>
          ) : (
            <form className="auth-form" onSubmit={submitAuth}>
              <div className="segmented" role="tablist" aria-label="Authentication mode">
                <button
                  type="button"
                  className={authMode === 'register' ? 'selected' : ''}
                  onClick={() => setAuthMode('register')}
                >
                  <UserPlus size={17} />
                  Register
                </button>
                <button
                  type="button"
                  className={authMode === 'login' ? 'selected' : ''}
                  onClick={() => setAuthMode('login')}
                >
                  <LogIn size={17} />
                  Login
                </button>
              </div>

              {authMode === 'register' && (
                <>
                  <label>
                    Display name
                    <input
                      name="displayName"
                      value={authForm.displayName}
                      onChange={updateAuthForm}
                      minLength="2"
                      maxLength="80"
                      required
                    />
                  </label>
                  <label>
                    Username
                    <input
                      name="username"
                      value={authForm.username}
                      onChange={updateAuthForm}
                      minLength="3"
                      maxLength="40"
                      pattern="[a-zA-Z0-9_]+"
                      required
                    />
                  </label>
                </>
              )}

              <label>
                Email
                <input
                  name="email"
                  type="email"
                  value={authForm.email}
                  onChange={updateAuthForm}
                  required
                />
              </label>
              <label>
                Password
                <input
                  name="password"
                  type="password"
                  value={authForm.password}
                  onChange={updateAuthForm}
                  minLength="8"
                  required
                />
              </label>

              <button className="submit-button" type="submit" disabled={authStatus.state === 'loading'}>
                {authStatus.state === 'loading' ? <Loader2 size={18} /> : <ShieldCheck size={18} />}
                {authMode === 'register' ? 'Create account' : 'Sign in'}
              </button>

              {authStatus.message && (
                <p className={`form-status ${authStatus.state}`}>{authStatus.message}</p>
              )}
            </form>
          )}
        </section>

        <section className="status-panel">
          <div className="panel-heading">
            <CheckCircle2 size={19} />
            <h2>Roadmap</h2>
          </div>
          <ol className="roadmap">
            {roadmapItems.map(([label, state]) => (
              <li className={state} key={label}>
                <span>{label}</span>
                <strong>{state}</strong>
              </li>
            ))}
          </ol>
        </section>

        <section className="api-panel">
          <div>
            <p className="eyebrow">Backend</p>
            <h2>{health.message}</h2>
            {health.timestamp && <p>{new Date(health.timestamp).toLocaleString()}</p>}
          </div>
          <button className="icon-button" type="button" onClick={checkHealth} aria-label="Refresh API status">
            <RefreshCw size={18} />
          </button>
        </section>
      </aside>
    </div>
  )
}

export default App
