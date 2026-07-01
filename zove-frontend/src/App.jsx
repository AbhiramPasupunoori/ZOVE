import { useEffect, useMemo, useRef, useState } from 'react'
import axios from 'axios'
import {
  Bell,
  Bookmark,
  CalendarDays,
  Check,
  Clapperboard,
  Compass,
  Hash,
  Heart,
  Home,
  ImagePlus,
  Loader2,
  Lock,
  LogIn,
  LogOut,
  MessageCircle,
  MoreHorizontal,
  Moon,
  Pause,
  PenLine,
  Play,
  Repeat2,
  Search,
  Send,
  ShieldCheck,
  Sparkles,
  Sun,
  UserPlus,
  Users,
  X,
} from 'lucide-react'
import cafeImg from './assets/feed-cafe.jpg'
import streetImg from './assets/feed-street.jpg'
import studioImg from './assets/feed-studio.jpg'
import zoveMark from './assets/zove-mark.svg'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'
const AUTH_STORAGE_KEY = 'zove.auth'
const PROFILE_STORAGE_KEY = 'zove.profile'
const THEME_STORAGE_KEY = 'zove.theme'

const api = axios.create({
  baseURL: API_BASE_URL,
})

const navItems = [
  { id: 'home', Icon: Home, label: 'Home' },
  { id: 'discover', Icon: Search, label: 'Discover' },
  { id: 'shorts', Icon: Clapperboard, label: 'Shorts' },
  { id: 'explore', Icon: Compass, label: 'Explore' },
  { id: 'circles', Icon: Users, label: 'Circles' },
  { id: 'chats', Icon: MessageCircle, label: 'Chats' },
  { id: 'alerts', Icon: Bell, label: 'Alerts' },
  { id: 'create', Icon: ImagePlus, label: 'Create' },
]

const viewCopy = {
  home: ['Home', 'What is moving today?'],
  discover: ['Discover', 'Find people, circles, and signals.'],
  shorts: ['Shorts', 'Profile videos playing now.'],
  explore: ['Explore', 'Visual moments from every corner.'],
  circles: ['Circles', 'Rooms where your people are live.'],
  chats: ['Chats', 'Keep the conversation moving.'],
  alerts: ['Alerts', 'Updates that need your attention.'],
  create: ['Create', 'Share something new.'],
}

const momentItems = [
  { name: 'Mira', note: 'Cafe sprint', image: cafeImg, tint: 'aqua' },
  { name: 'Rohan', note: 'Rain walk', image: streetImg, tint: 'sunset' },
  { name: 'Studio', note: 'New loop', image: studioImg, tint: 'violet' },
]

const shortItems = [
  {
    id: 'aanya-motion',
    author: 'Aanya Rao',
    username: 'aanya.moves',
    title: 'Campus fit check before the meetup',
    caption: 'Three cuts, one hallway, and a whole lot of motion notes.',
    image: studioImg,
    sound: 'Original audio - Aanya',
    views: '42K',
    likes: 12800,
    comments: 344,
  },
  {
    id: 'kai-street',
    author: 'Kai Brooks',
    username: 'kai.frames',
    title: 'Rainy street frames in 12 seconds',
    caption: 'Golden reflections, quick steps, and a city that knows its angles.',
    image: streetImg,
    sound: 'City loop - Kai',
    views: '31K',
    likes: 9300,
    comments: 211,
  },
  {
    id: 'mira-cafe-short',
    author: 'Mira Chen',
    username: 'mira',
    title: 'Coffee desk reset',
    caption: 'Tiny reset before a long build session.',
    image: cafeImg,
    sound: 'Soft keys - Mira',
    views: '24K',
    likes: 7600,
    comments: 182,
  },
]

const initialFeedItems = [
  {
    id: 'mira-cafe',
    author: 'Mira Chen',
    username: 'mira',
    time: '12m',
    circle: 'Daily Makers',
    image: cafeImg,
    text: 'A quiet table somehow turned into a launch pad. Shipping the profile flow today, then taking this coffee for a walk.',
    quote: 'Small rituals keep big projects moving.',
    reactionCount: 12400,
    replyCount: 148,
    shareCount: 382,
    tags: ['#makers', '#coffee'],
    comments: [
      { id: 'mira-comment-1', author: 'Aanya Rao', text: 'This table energy is exactly the push I needed.' },
    ],
  },
  {
    id: 'rohan-rain',
    author: 'Rohan Mehta',
    username: 'rohan',
    time: '31m',
    circle: 'City Notes',
    image: streetImg,
    text: 'Golden-hour streets after rain are basically a public mood board. Saving this palette for the next ZOVE theme pass.',
    quote: 'The city after rain has its own filter.',
    reactionCount: 8900,
    replyCount: 96,
    shareCount: 211,
    tags: ['#streetframes', '#citynotes'],
    comments: [
      { id: 'rohan-comment-1', author: 'Kai Brooks', text: 'That warm light on the buildings is unreal.' },
    ],
  },
  {
    id: 'studio-loop',
    author: 'ZOVE Studio',
    username: 'zove.studio',
    time: '1h',
    circle: 'Creator Desk',
    image: studioImg,
    text: 'Workspace check: one keyboard, one track idea, three open tabs, and a calmer feed surface taking shape.',
    quote: 'Build the room you want to think inside.',
    reactionCount: 18200,
    replyCount: 312,
    shareCount: 704,
    tags: ['#studio', '#creatordesk'],
    comments: [
      { id: 'studio-comment-1', author: 'Leela Shah', text: 'Calmer feed surface sounds like a win.' },
    ],
  },
]

const feedFilters = [
  { id: 'all', label: 'All' },
  { id: 'following', label: 'Following' },
  { id: 'saved', label: 'Saved' },
]

const circles = [
  { name: 'Design Room', meta: '2.8K live', note: 'Screens, logos, product polish, and fast feedback.' },
  { name: 'Campus Feed', meta: '812 live', note: 'Clubs, classes, late-night food runs, and plans.' },
  { name: 'Music Makers', meta: '1.4K live', note: 'Loops, playlists, show notes, and studio rooms.' },
]

const suggestions = [
  { name: 'Aanya Rao', note: 'movement notes' },
  { name: 'Kai Brooks', note: 'street frames' },
  { name: 'Leela Shah', note: 'quiet essays' },
]

const initialConversations = [
  {
    id: 'aanya',
    name: 'Aanya Rao',
    handle: 'aanya.moves',
    status: 'Online',
    messages: [
      { id: 'aanya-1', from: 'them', text: 'Are you joining the creator meetup tonight?', time: '7:42 PM' },
      { id: 'aanya-2', from: 'me', text: 'Yes. I am bringing the new Shorts layout too.', time: '7:44 PM' },
      { id: 'aanya-3', from: 'them', text: 'Perfect. Send me the preview when it is ready.', time: '7:45 PM' },
    ],
  },
  {
    id: 'kai',
    name: 'Kai Brooks',
    handle: 'kai.frames',
    status: 'Typing soon',
    messages: [
      { id: 'kai-1', from: 'them', text: 'The street frames from yesterday are ready.', time: '6:18 PM' },
      { id: 'kai-2', from: 'me', text: 'Drop the best one here. I want it for Explore.', time: '6:20 PM' },
    ],
  },
  {
    id: 'leela',
    name: 'Leela Shah',
    handle: 'leela.notes',
    status: 'Last seen 12m ago',
    messages: [
      { id: 'leela-1', from: 'them', text: 'I wrote a quieter caption for the cafe post.', time: '5:03 PM' },
      { id: 'leela-2', from: 'me', text: 'Nice. Send it here and I will test it in the feed.', time: '5:05 PM' },
    ],
  },
]

const trendingTopics = [
  { label: '#campus', meta: '18.4K posts' },
  { label: '#design', meta: '9.7K posts' },
  { label: '#music', meta: '6.2K posts' },
  { label: '#coffee', meta: '4.8K posts' },
]

const alerts = [
  'Mira reacted to your cafe sprint.',
  'Design Room is hosting a quick critique.',
  'Aanya shared a new movement note.',
]

function readStoredAuth() {
  try {
    return JSON.parse(localStorage.getItem(AUTH_STORAGE_KEY))
  } catch {
    return null
  }
}

function readStoredProfile() {
  try {
    return JSON.parse(localStorage.getItem(PROFILE_STORAGE_KEY))
  } catch {
    return null
  }
}

function getInitial(name) {
  return (name || 'Z').charAt(0).toUpperCase()
}

function formatCount(value) {
  if (value >= 1000) {
    return `${(value / 1000).toFixed(value >= 10000 ? 1 : 1).replace('.0', '')}K`
  }

  return String(value)
}

function App() {
  const [activeView, setActiveView] = useState('home')
  const [theme, setTheme] = useState(() => localStorage.getItem(THEME_STORAGE_KEY) ?? 'light')
  const [authMode, setAuthMode] = useState('register')
  const [authForm, setAuthForm] = useState({
    displayName: '',
    username: '',
    email: '',
    password: '',
  })
  const [profile, setProfile] = useState(
    () =>
      readStoredProfile() ?? {
        displayName: 'Guest User',
        username: 'zove_guest',
        bio: 'Building moments, circles, and signals on ZOVE.',
      },
  )
  const [profileDraft, setProfileDraft] = useState(profile)
  const [isEditingProfile, setIsEditingProfile] = useState(false)
  const [auth, setAuth] = useState(readStoredAuth)
  const [authStatus, setAuthStatus] = useState({ state: 'idle', message: '' })
  const [posts, setPosts] = useState(initialFeedItems)
  const [feedFilter, setFeedFilter] = useState('all')
  const [composerText, setComposerText] = useState('')
  const [composerNotice, setComposerNotice] = useState('')
  const [composerTools, setComposerTools] = useState({
    moment: false,
    circle: false,
    signal: false,
  })
  const [activeMoment, setActiveMoment] = useState('')
  const [activeCircle, setActiveCircle] = useState('All')
  const [searchQuery, setSearchQuery] = useState('')
  const [followedPeople, setFollowedPeople] = useState([])
  const [signalsTuned, setSignalsTuned] = useState(false)
  const [alertsRead, setAlertsRead] = useState(false)
  const [openMenu, setOpenMenu] = useState('')
  const [openComments, setOpenComments] = useState('')
  const [commentDrafts, setCommentDrafts] = useState({})
  const [activeShortId, setActiveShortId] = useState(shortItems[0].id)
  const [playingShorts, setPlayingShorts] = useState([shortItems[0].id])
  const [likedShorts, setLikedShorts] = useState([])
  const [savedShorts, setSavedShorts] = useState([])
  const [conversations, setConversations] = useState(initialConversations)
  const [activeConversationId, setActiveConversationId] = useState(initialConversations[0].id)
  const [messageDraft, setMessageDraft] = useState('')
  const [toast, setToast] = useState('')

  const composerRef = useRef(null)
  const searchRef = useRef(null)

  const currentUser = auth?.user
  const profileName = profile.displayName || currentUser?.displayName || 'Guest User'
  const profileHandle = profile.username || currentUser?.username || 'zove_guest'
  const profileEmail = currentUser?.email ?? 'Sign in to sync your profile'
  const profileBio = profile.bio || 'Share life as it happens.'
  const [eyebrow, heading] = viewCopy[activeView]

  const filteredSuggestions = useMemo(() => {
    const query = searchQuery.trim().toLowerCase()

    if (!query) {
      return suggestions
    }

    return suggestions.filter(({ name, note }) => `${name} ${note}`.toLowerCase().includes(query))
  }, [searchQuery])

  const filteredCircles = useMemo(() => {
    const query = searchQuery.trim().toLowerCase()

    if (!query) {
      return circles
    }

    return circles.filter(({ name, note }) => `${name} ${note}`.toLowerCase().includes(query))
  }, [searchQuery])

  const filteredTopics = useMemo(() => {
    const query = searchQuery.trim().toLowerCase().replace('#', '')

    if (!query) {
      return trendingTopics
    }

    return trendingTopics.filter(({ label }) => label.toLowerCase().includes(query))
  }, [searchQuery])

  const visiblePosts = useMemo(() => {
    if (feedFilter === 'saved') {
      return posts.filter((post) => post.saved)
    }

    if (feedFilter === 'following') {
      return posts.filter((post) => followedPeople.includes(post.author) || post.username === profileHandle)
    }

    return posts
  }, [feedFilter, followedPeople, posts, profileHandle])

  const savedPostCount = posts.filter((post) => post.saved).length
  const activeConversation = conversations.find((conversation) => conversation.id === activeConversationId) ?? conversations[0]

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

  useEffect(() => {
    if (!toast) {
      return undefined
    }

    const timeoutId = window.setTimeout(() => setToast(''), 2200)

    return () => window.clearTimeout(timeoutId)
  }, [toast])

  useEffect(() => {
    localStorage.setItem(PROFILE_STORAGE_KEY, JSON.stringify(profile))
  }, [profile])

  useEffect(() => {
    localStorage.setItem(THEME_STORAGE_KEY, theme)
  }, [theme])

  function showToast(message) {
    setToast(message)
  }

  function toggleTheme() {
    const nextTheme = theme === 'dark' ? 'light' : 'dark'
    setTheme(nextTheme)
    showToast(nextTheme === 'dark' ? 'Dark mode on' : 'Light mode on')
  }

  function selectView(viewId) {
    setActiveView(viewId)
    setOpenMenu('')

    if (viewId === 'create') {
      window.setTimeout(() => composerRef.current?.focus(), 0)
    }

    if (viewId === 'discover') {
      window.setTimeout(() => searchRef.current?.focus(), 0)
    }
  }

  function selectShort(shortId) {
    setActiveShortId(shortId)
    setPlayingShorts((items) => (items.includes(shortId) ? items : [...items, shortId]))
  }

  function toggleShortPlayback(shortId) {
    setPlayingShorts((items) => (
      items.includes(shortId) ? items.filter((itemId) => itemId !== shortId) : [...items, shortId]
    ))
  }

  function toggleShortLike(shortId) {
    setLikedShorts((items) => (
      items.includes(shortId) ? items.filter((itemId) => itemId !== shortId) : [...items, shortId]
    ))
  }

  function toggleShortSave(shortId) {
    const isSaved = savedShorts.includes(shortId)
    setSavedShorts((items) => (
      isSaved ? items.filter((itemId) => itemId !== shortId) : [...items, shortId]
    ))
    showToast(isSaved ? 'Short removed from saved' : 'Short saved')
  }

  function updateAuthForm(event) {
    const { name, value } = event.target
    setAuthForm((form) => ({ ...form, [name]: value }))
  }

  function updateProfileDraft(event) {
    const { name, value } = event.target
    setProfileDraft((draft) => ({ ...draft, [name]: value }))
  }

  function saveProfile(event) {
    event.preventDefault()
    setProfile({
      displayName: profileDraft.displayName.trim() || 'Guest User',
      username: profileDraft.username.trim().replace(/^@/, '') || 'zove_guest',
      bio: profileDraft.bio.trim() || 'Share life as it happens.',
    })
    setIsEditingProfile(false)
    showToast('Profile updated')
  }

  function cancelProfileEdit() {
    setProfileDraft(profile)
    setIsEditingProfile(false)
  }

  function chooseFeedFilter(filterId) {
    setFeedFilter(filterId)
    setActiveView('home')
    showToast(filterId === 'saved' ? 'Showing saved posts' : `${feedFilters.find(({ id }) => id === filterId)?.label} feed`)
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
      showToast(authMode === 'register' ? 'Account created' : 'Signed in')
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
    showToast('Signed out')
  }

  function toggleComposerTool(tool) {
    setComposerTools((tools) => ({ ...tools, [tool]: !tools[tool] }))
    setActiveView('create')
  }

  function chooseMoment(moment) {
    setActiveMoment(moment.name)
    setComposerTools((tools) => ({ ...tools, moment: true }))
    setComposerText((text) => text || `${moment.note}: `)
    setActiveView('create')
    window.setTimeout(() => composerRef.current?.focus(), 0)
    showToast(`${moment.name}'s moment attached`)
  }

  function publishPost() {
    const text = composerText.trim()

    if (!text) {
      setComposerNotice('Write something before publishing.')
      composerRef.current?.focus()
      return
    }

    const selectedMoment = momentItems.find((moment) => moment.name === activeMoment)
    const newPost = {
      id: `local-${Date.now()}`,
      author: profileName,
      username: profileHandle,
      time: 'now',
      circle: composerTools.circle ? activeCircle === 'All' ? 'Your Circle' : activeCircle : 'Home',
      image: composerTools.moment ? selectedMoment?.image ?? cafeImg : undefined,
      text,
      quote: composerTools.signal ? 'Fresh signal from your circle.' : '',
      reactionCount: 0,
      replyCount: 0,
      shareCount: 0,
      tags: composerTools.signal ? ['#signal'] : ['#zove'],
      comments: [],
      liked: false,
      shared: false,
      saved: false,
    }

    setPosts((items) => [newPost, ...items])
    setComposerText('')
    setComposerNotice('Published to your home feed.')
    setComposerTools({ moment: false, circle: false, signal: false })
    setActiveMoment('')
    setActiveView('home')
    showToast('Post published')
  }

  function updateCommentDraft(postId, value) {
    setCommentDrafts((drafts) => ({ ...drafts, [postId]: value }))
  }

  function toggleComments(postId) {
    setOpenComments((currentPostId) => (currentPostId === postId ? '' : postId))
  }

  function submitComment(event, item) {
    event.preventDefault()

    const text = commentDrafts[item.id]?.trim()

    if (!text) {
      showToast('Write a comment first')
      return
    }

    updatePost(item.id, (post) => ({
      ...post,
      replyCount: post.replyCount + 1,
      comments: [
        ...(post.comments ?? []),
        {
          id: `${post.id}-comment-${Date.now()}`,
          author: profileName,
          text,
        },
      ],
    }))
    setCommentDrafts((drafts) => ({ ...drafts, [item.id]: '' }))
    setOpenComments(item.id)
    showToast('Comment added')
  }

  function updatePost(postId, updater) {
    setPosts((items) => items.map((item) => (item.id === postId ? updater(item) : item)))
  }

  function toggleLike(postId) {
    updatePost(postId, (item) => ({
      ...item,
      liked: !item.liked,
      reactionCount: item.reactionCount + (item.liked ? -1 : 1),
    }))
  }

  function sharePost(postId) {
    updatePost(postId, (item) => ({
      ...item,
      shared: true,
      shareCount: item.shareCount + (item.shared ? 0 : 1),
    }))
    showToast('Shared to your circle')
  }

  function toggleSave(postId) {
    updatePost(postId, (item) => ({ ...item, saved: !item.saved }))
  }

  function copyPostLink(postId) {
    const link = `${window.location.origin}/post/${postId}`
    navigator.clipboard?.writeText(link).catch(() => undefined)
    setOpenMenu('')
    showToast('Post link copied')
  }

  function selectCircle(name) {
    setActiveCircle(name)
    setComposerTools((tools) => ({ ...tools, circle: name !== 'All' }))
    setActiveView('circles')
    showToast(name === 'All' ? 'Showing all circles' : `${name} selected`)
  }

  function toggleFollow(name) {
    const isFollowed = followedPeople.includes(name)

    setFollowedPeople((people) => (isFollowed ? people.filter((person) => person !== name) : [...people, name]))
    showToast(isFollowed ? `${name} removed` : `${name} added`)
  }

  function selectTopic(label) {
    setSearchQuery(label.replace('#', ''))
    setActiveView('discover')
    window.setTimeout(() => searchRef.current?.focus(), 0)
    showToast(`${label} opened`)
  }

  function sendChatMessage(event) {
    event.preventDefault()

    const text = messageDraft.trim()

    if (!text) {
      showToast('Type a message first')
      return
    }

    const newMessage = {
      id: `${activeConversationId}-${Date.now()}`,
      from: 'me',
      text,
      time: new Date().toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' }),
    }

    setConversations((items) =>
      items.map((conversation) =>
        conversation.id === activeConversationId
          ? { ...conversation, messages: [...conversation.messages, newMessage] }
          : conversation,
      ),
    )
    setMessageDraft('')
  }

  function renderViewPanel() {
    if (activeView === 'home' || activeView === 'create') {
      return null
    }

    if (activeView === 'discover') {
      return (
        <section className="view-panel" aria-label="Discover tools">
          <label className="search-box">
            <Search size={18} />
            <input
              ref={searchRef}
              type="search"
              value={searchQuery}
              onChange={(event) => setSearchQuery(event.target.value)}
              placeholder="Search people and circles"
            />
          </label>
          <div className="chip-grid">
            {filteredSuggestions.map(({ name, note }) => (
              <button type="button" key={name} onClick={() => toggleFollow(name)}>
                <span className="avatar tiny">{getInitial(name)}</span>
                <span>
                  <strong>{name}</strong>
                  <small>{followedPeople.includes(name) ? 'Following' : note}</small>
                </span>
              </button>
            ))}
          </div>
          <div className="trend-list compact">
            {filteredTopics.map(({ label, meta }) => (
              <button type="button" key={label} onClick={() => selectTopic(label)}>
                <span>{label}</span>
                <small>{meta}</small>
              </button>
            ))}
          </div>
        </section>
      )
    }

    if (activeView === 'shorts') {
      return (
        <section className="shorts-view" aria-label="ZOVE shorts">
          <div className="shorts-rail" aria-label="Shorts list">
            {shortItems.map((short) => {
              const isActive = activeShortId === short.id
              const isPlaying = playingShorts.includes(short.id)
              const isLiked = likedShorts.includes(short.id)
              const isSaved = savedShorts.includes(short.id)
              const isFollowed = followedPeople.includes(short.author)

              return (
                <article
                  className={`short-card ${isActive ? 'active' : ''}`}
                  key={short.id}
                  onMouseEnter={() => selectShort(short.id)}
                >
                  <button
                    className="short-video"
                    type="button"
                    onClick={() => toggleShortPlayback(short.id)}
                    aria-label={`${isPlaying ? 'Pause' : 'Play'} ${short.title}`}
                  >
                    <img src={short.image} alt="" />
                    <span className="short-gradient" />
                    <span className="short-play-state">
                      {isPlaying ? <Pause size={20} /> : <Play size={20} />}
                      {isPlaying ? 'Playing' : 'Paused'}
                    </span>
                    <span className="short-progress" />
                    <span className="short-copy">
                      <strong>{short.title}</strong>
                      <small>@{short.username} · {short.views} views</small>
                    </span>
                  </button>

                  <div className="short-actions">
                    <button className={isLiked ? 'active' : ''} type="button" onClick={() => toggleShortLike(short.id)}>
                      <Heart size={18} />
                      {formatCount(short.likes + (isLiked ? 1 : 0))}
                    </button>
                    <button type="button" onClick={() => {
                      setComposerText(`@${short.username} `)
                      setActiveView('create')
                      window.setTimeout(() => composerRef.current?.focus(), 0)
                    }}>
                      <MessageCircle size={18} />
                      {formatCount(short.comments)}
                    </button>
                    <button className={isSaved ? 'active' : ''} type="button" onClick={() => toggleShortSave(short.id)}>
                      <Bookmark size={18} />
                      {isSaved ? 'Saved' : 'Save'}
                    </button>
                    <button className={isFollowed ? 'active' : ''} type="button" onClick={() => toggleFollow(short.author)}>
                      <UserPlus size={18} />
                      {isFollowed ? 'Following' : 'Follow'}
                    </button>
                  </div>

                  <footer className="short-footer">
                    <div className="avatar tiny">{getInitial(short.author)}</div>
                    <div>
                      <strong>{short.author}</strong>
                      <p>{short.caption}</p>
                      <small>{short.sound}</small>
                    </div>
                  </footer>
                </article>
              )
            })}
          </div>
        </section>
      )
    }

    if (activeView === 'explore') {
      return (
        <section className="view-panel compact" aria-label="Explore filters">
          {momentItems.map((moment) => (
            <button type="button" className="info-button" key={moment.name} onClick={() => chooseMoment(moment)}>
              <ImagePlus size={18} />
              Attach {moment.name}
            </button>
          ))}
        </section>
      )
    }

    if (activeView === 'circles') {
      return (
        <section className="view-panel" aria-label="Circle details">
          <div>
            <p className="eyebrow">{activeCircle}</p>
            <h2>{activeCircle === 'All' ? 'All live circles' : circles.find(({ name }) => name === activeCircle)?.note}</h2>
          </div>
          <div className="chip-grid">
            {filteredCircles.map(({ name, meta }) => (
              <button type="button" key={name} onClick={() => selectCircle(name)}>
                <span className="circle-icon"><Users size={17} /></span>
                <span>
                  <strong>{name}</strong>
                  <small>{meta}</small>
                </span>
              </button>
            ))}
          </div>
        </section>
      )
    }

    if (activeView === 'chats') {
      return (
        <section className="chat-shell" aria-label="Chats">
          <div className="chat-list" aria-label="Conversations">
            {conversations.map((conversation) => {
              const lastMessage = conversation.messages.at(-1)
              const isActive = activeConversation.id === conversation.id

              return (
                <button
                  className={`chat-contact ${isActive ? 'active' : ''}`}
                  type="button"
                  key={conversation.id}
                  onClick={() => setActiveConversationId(conversation.id)}
                >
                  <span className="avatar tiny">{getInitial(conversation.name)}</span>
                  <span>
                    <strong>{conversation.name}</strong>
                    <small>{lastMessage?.text}</small>
                  </span>
                  <em>{lastMessage?.time}</em>
                </button>
              )
            })}
          </div>

          <div className="chat-window">
            <header className="chat-header">
              <div className="avatar small">{getInitial(activeConversation.name)}</div>
              <div>
                <h2>{activeConversation.name}</h2>
                <p>@{activeConversation.handle} · {activeConversation.status}</p>
              </div>
            </header>

            <div className="chat-thread" aria-label={`Conversation with ${activeConversation.name}`}>
              {activeConversation.messages.map((message) => (
                <div className={`chat-message ${message.from === 'me' ? 'mine' : ''}`} key={message.id}>
                  <p>{message.text}</p>
                  <time>{message.time}</time>
                </div>
              ))}
            </div>

            <form className="chat-input" onSubmit={sendChatMessage}>
              <input
                value={messageDraft}
                onChange={(event) => setMessageDraft(event.target.value)}
                placeholder={`Message ${activeConversation.name}`}
              />
              <button type="submit">
                <Send size={18} />
                Send
              </button>
            </form>
          </div>
        </section>
      )
    }

    if (activeView === 'alerts') {
      return (
        <section className="view-panel" aria-label="Alerts">
          <div className="rail-heading">
            <h2>{alertsRead ? 'All caught up' : `${alerts.length} new alerts`}</h2>
            <button type="button" onClick={() => {
              setAlertsRead(true)
              showToast('Alerts marked as read')
            }}>
              Mark read
            </button>
          </div>
          <div className="mini-list">
            {(alertsRead ? ['No new alerts right now.'] : alerts).map((alert) => (
              <p key={alert}>{alert}</p>
            ))}
          </div>
        </section>
      )
    }

    return null
  }

  return (
    <div className={`app-shell ${theme === 'dark' ? 'theme-dark' : ''} ${activeView === 'chats' ? 'chat-mode' : ''}`}>
      <aside className="side-dock">
        <div className="brand" aria-label="ZOVE">
          <span className="brand-mark">
            <img src={zoveMark} alt="" />
          </span>
          <span>
            <strong>ZOVE</strong>
            <small>Share life as it happens.</small>
          </span>
        </div>

        <nav className="nav" aria-label="Primary navigation">
          {navItems.map(({ Icon, id, label }) => (
            <button
              className={`nav-button ${activeView === id ? 'active' : ''}`}
              type="button"
              key={id}
              onClick={() => selectView(id)}
              aria-current={activeView === id ? 'page' : undefined}
            >
              <Icon size={21} />
              <span>{label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <main className="feed-column">
        <header className="top-bar">
          <div>
            <p className="eyebrow">{eyebrow}</p>
            <h1>{heading}</h1>
          </div>
          <div className="top-actions">
            <button className="ghost-button" type="button" onClick={() => chooseFeedFilter('saved')}>
              <Bookmark size={17} />
              Saved {savedPostCount ? savedPostCount : ''}
            </button>
            <button
              className="icon-button theme-button"
              type="button"
              onClick={toggleTheme}
              aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
            >
              {theme === 'dark' ? <Sun size={19} /> : <Moon size={19} />}
            </button>
          </div>
        </header>

        {renderViewPanel()}

        {!['chats', 'shorts'].includes(activeView) && (
          <>
            <section className="composer" aria-label="Create post">
              <div className="avatar current">{getInitial(profileName)}</div>
              <div className="composer-body">
                <textarea
                  ref={composerRef}
                  rows="3"
                  value={composerText}
                  onChange={(event) => {
                    setComposerText(event.target.value)
                    setComposerNotice('')
                  }}
                  placeholder="Share something new"
                />
                <div className="composer-actions">
                  <div className="composer-tools">
                    <button
                      className={composerTools.moment ? 'selected' : ''}
                      type="button"
                      onClick={() => toggleComposerTool('moment')}
                      aria-pressed={composerTools.moment}
                    >
                      <ImagePlus size={18} />
                      Moment
                    </button>
                    <button
                      className={composerTools.circle ? 'selected' : ''}
                      type="button"
                      onClick={() => toggleComposerTool('circle')}
                      aria-pressed={composerTools.circle}
                    >
                      <Users size={18} />
                      Circle
                    </button>
                    <button
                      className={composerTools.signal ? 'selected' : ''}
                      type="button"
                      onClick={() => toggleComposerTool('signal')}
                      aria-pressed={composerTools.signal}
                    >
                      <Sparkles size={18} />
                      Signal
                    </button>
                  </div>
                  <button className="post-button" type="button" onClick={publishPost}>
                    <Send size={17} />
                    Publish
                  </button>
                </div>
                {composerNotice && <p className="composer-note">{composerNotice}</p>}
              </div>
            </section>

            <section className="moments-strip" aria-label="Moments">
              {momentItems.map((moment) => (
                <button
                  className={`moment-card ${moment.tint} ${activeMoment === moment.name ? 'selected' : ''}`}
                  type="button"
                  key={moment.name}
                  onClick={() => chooseMoment(moment)}
                >
                  <img src={moment.image} alt="" />
                  <span>
                    <strong>{moment.name}</strong>
                    <small>{moment.note}</small>
                  </span>
                </button>
              ))}
            </section>

            <section className="feed-controls" aria-label="Feed filters">
              <div className="segmented feed-tabs">
                {feedFilters.map(({ id, label }) => (
              <button
                className={feedFilter === id ? 'selected' : ''}
                type="button"
                key={id}
                onClick={() => chooseFeedFilter(id)}
              >
                {label}
              </button>
                ))}
              </div>
              <span>{visiblePosts.length} posts</span>
            </section>

            <section className="feed-list" aria-label="ZOVE feed">
              {visiblePosts.length === 0 && (
                <div className="empty-state">
                  <Bookmark size={22} />
                  <strong>No posts here yet</strong>
                  <p>{feedFilter === 'saved' ? 'Save a post to build your collection.' : 'Follow creators to shape this feed.'}</p>
                </div>
              )}

              {visiblePosts.map((item) => (
                <article className="feed-card" key={item.id}>
                  <header className="feed-header">
                    <div className="avatar small">{getInitial(item.author)}</div>
                    <div className="feed-meta">
                      <h2>{item.author}</h2>
                      <p>@{item.username} · {item.circle}</p>
                    </div>
                    <time>{item.time}</time>
                    <div className="more-wrap">
                      <button
                        className="icon-button"
                        type="button"
                        aria-label={`More options for ${item.author}`}
                        aria-expanded={openMenu === item.id}
                        onClick={() => setOpenMenu((menuId) => (menuId === item.id ? '' : item.id))}
                      >
                        <MoreHorizontal size={21} />
                      </button>
                      {openMenu === item.id && (
                        <div className="more-menu">
                          <button type="button" onClick={() => copyPostLink(item.id)}>Copy link</button>
                          <button type="button" onClick={() => {
                            setOpenMenu('')
                            showToast(`${item.author} muted`)
                          }}>
                            Mute updates
                          </button>
                        </div>
                      )}
                    </div>
                  </header>

                  <p className="feed-text">{item.text}</p>

                  {(item.image || item.quote) && (
                    <div className="media-frame">
                      {item.image && <img src={item.image} alt={`${item.author} shared moment`} />}
                      {item.quote && <blockquote>{item.quote}</blockquote>}
                    </div>
                  )}

                  <footer className="feed-actions">
                    <button className={item.liked ? 'active' : ''} type="button" onClick={() => toggleLike(item.id)}>
                      <Heart size={19} />
                      {formatCount(item.reactionCount)}
                    </button>
                    <button className={openComments === item.id ? 'active' : ''} type="button" onClick={() => toggleComments(item.id)}>
                      <MessageCircle size={19} />
                      {formatCount(item.replyCount)}
                    </button>
                    <button className={item.shared ? 'active' : ''} type="button" onClick={() => sharePost(item.id)}>
                      <Repeat2 size={19} />
                      {formatCount(item.shareCount)}
                    </button>
                    <button className={item.saved ? 'active' : ''} type="button" onClick={() => toggleSave(item.id)}>
                      <Bookmark size={19} />
                      {item.saved ? 'Saved' : 'Save'}
                    </button>
                  </footer>

                  {openComments === item.id && (
                    <section className="comments-panel" aria-label={`Comments on ${item.author}'s post`}>
                      <div className="comment-list">
                        {(item.comments ?? []).map((comment) => (
                          <div className="comment-row" key={comment.id}>
                            <span className="avatar tiny">{getInitial(comment.author)}</span>
                            <p>
                              <strong>{comment.author}</strong>
                              {comment.text}
                            </p>
                          </div>
                        ))}
                      </div>
                      <form className="comment-form" onSubmit={(event) => submitComment(event, item)}>
                        <input
                          value={commentDrafts[item.id] ?? ''}
                          onChange={(event) => updateCommentDraft(item.id, event.target.value)}
                          placeholder={`Comment on ${item.author}'s post`}
                        />
                        <button type="submit">
                          <Send size={16} />
                        </button>
                      </form>
                    </section>
                  )}
                </article>
              ))}
            </section>
          </>
        )}
      </main>

      <aside className="right-rail">
        <section className="profile-panel">
          <div className="profile-row">
            <div className="avatar profile">{getInitial(profileName)}</div>
            <div>
              <h2>{profileName}</h2>
              <p>@{profileHandle}</p>
              <p>{profileEmail}</p>
            </div>
          </div>
          <p className="profile-bio">{profileBio}</p>

          <div className="profile-actions">
            <button
              className="outline-button"
              type="button"
              onClick={() => {
                setProfileDraft(profile)
                setIsEditingProfile(true)
              }}
            >
              <PenLine size={17} />
              Edit profile
            </button>
            <button className="outline-button" type="button" onClick={() => chooseFeedFilter('saved')}>
              <Bookmark size={17} />
              Saved
            </button>
          </div>

          {isEditingProfile && (
            <form className="profile-editor" onSubmit={saveProfile}>
              <label>
                Display name
                <input
                  name="displayName"
                  value={profileDraft.displayName}
                  onChange={updateProfileDraft}
                  minLength="2"
                  maxLength="80"
                  required
                />
              </label>
              <label>
                Username
                <input
                  name="username"
                  value={profileDraft.username}
                  onChange={updateProfileDraft}
                  minLength="3"
                  maxLength="40"
                  pattern="[a-zA-Z0-9_]+"
                  required
                />
              </label>
              <label>
                Bio
                <input name="bio" value={profileDraft.bio} onChange={updateProfileDraft} maxLength="120" />
              </label>
              <div className="profile-editor-actions">
                <button className="submit-button" type="submit">
                  <Check size={17} />
                  Save
                </button>
                <button className="outline-button" type="button" onClick={cancelProfileEdit}>
                  <X size={17} />
                  Cancel
                </button>
              </div>
            </form>
          )}

          {currentUser ? (
            <button className="outline-button" type="button" onClick={logout}>
              <LogOut size={18} />
              Sign out
            </button>
          ) : (
            <form className="auth-form" onSubmit={submitAuth}>
              <div className="panel-heading">
                <Lock size={18} />
                <h3>{authMode === 'register' ? 'Join ZOVE' : 'Welcome back'}</h3>
              </div>

              <div className="segmented" role="tablist" aria-label="Authentication mode">
                <button
                  type="button"
                  className={authMode === 'register' ? 'selected' : ''}
                  onClick={() => setAuthMode('register')}
                >
                  <UserPlus size={16} />
                  Register
                </button>
                <button
                  type="button"
                  className={authMode === 'login' ? 'selected' : ''}
                  onClick={() => setAuthMode('login')}
                >
                  <LogIn size={16} />
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
                <input name="email" type="email" value={authForm.email} onChange={updateAuthForm} required />
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

              {authStatus.message && <p className={`form-status ${authStatus.state}`}>{authStatus.message}</p>}
            </form>
          )}
        </section>

        <section className="circles-panel">
          <div className="rail-heading">
            <h3>Live circles</h3>
            <button
              className={activeCircle === 'All' ? 'active' : ''}
              type="button"
              onClick={() => selectCircle('All')}
            >
              All
            </button>
          </div>
          {circles.map(({ name, meta }) => (
            <button
              className={`circle-row ${activeCircle === name ? 'active' : ''}`}
              type="button"
              key={name}
              onClick={() => selectCircle(name)}
            >
              <span className="circle-icon"><Users size={17} /></span>
              <span>
                <strong>{name}</strong>
                <small>{meta}</small>
              </span>
            </button>
          ))}
        </section>

        <section className="trends-panel">
          <div className="rail-heading">
            <h3>Trending</h3>
            <Hash size={18} />
          </div>
          <div className="trend-list">
            {trendingTopics.map(({ label, meta }) => (
              <button type="button" key={label} onClick={() => selectTopic(label)}>
                <span>{label}</span>
                <small>{meta}</small>
              </button>
            ))}
          </div>
        </section>

        <section className="signals-panel">
          <div className="rail-heading">
            <h3>Signals</h3>
            <button
              className={signalsTuned ? 'active' : ''}
              type="button"
              onClick={() => {
                setSignalsTuned((value) => !value)
                showToast(signalsTuned ? 'Signals reset' : 'Signals tuned')
              }}
            >
              {signalsTuned ? 'Tuned' : 'Tune'}
            </button>
          </div>
          <button className="signal-row" type="button" onClick={() => showToast('Creator meetup saved')}>
            <CalendarDays size={18} />
            <span>Creator meetup tonight</span>
          </button>
          {suggestions.map(({ name, note }) => {
            const isFollowed = followedPeople.includes(name)

            return (
              <div className="suggestion-row" key={name}>
                <div className="avatar tiny">{getInitial(name)}</div>
                <div>
                  <strong>{name}</strong>
                  <p>{note}</p>
                </div>
                <button className={isFollowed ? 'added' : ''} type="button" onClick={() => toggleFollow(name)}>
                  {isFollowed ? 'Added' : 'Add'}
                </button>
              </div>
            )
          })}
        </section>
      </aside>

      <div className={`toast ${toast ? 'visible' : ''}`} role="status" aria-live="polite">
        {toast}
      </div>
    </div>
  )
}

export default App
