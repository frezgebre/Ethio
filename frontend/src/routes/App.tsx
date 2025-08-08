import { useEffect, useMemo, useState } from 'react'
import { formatDistanceToNow } from 'date-fns'
import { apiClient } from '@/utils/apiClient'

type StatusCheck = {
  id: string
  client_name: string
  timestamp: string
}

function useTheme() {
  const [dark, setDark] = useState(() => {
    return localStorage.getItem('theme') === 'dark' || (
      localStorage.getItem('theme') === null &&
      window.matchMedia('(prefers-color-scheme: dark)').matches
    )
  })

  useEffect(() => {
    const root = document.documentElement
    if (dark) {
      root.classList.add('dark')
      localStorage.setItem('theme', 'dark')
    } else {
      root.classList.remove('dark')
      localStorage.setItem('theme', 'light')
    }
  }, [dark])

  return { dark, setDark }
}

export default function App() {
  const { dark, setDark } = useTheme()
  const [clientName, setClientName] = useState('web-ui')
  const [loading, setLoading] = useState(false)
  const [items, setItems] = useState<StatusCheck[]>([])
  const [error, setError] = useState<string | null>(null)

  const backendInfo = useMemo(() => {
    const url = import.meta.env.VITE_BACKEND_URL as string | undefined
    return {
      baseURL: apiClient.defaults.baseURL,
      configuredURL: url ?? 'same-origin',
    }
  }, [])

  async function fetchItems() {
    setError(null)
    try {
      const res = await apiClient.get<StatusCheck[]>('/status')
      setItems(res.data)
    } catch (e: any) {
      setError(e?.message ?? 'Failed to load')
    }
  }

  async function createStatus() {
    setLoading(true)
    setError(null)
    try {
      await apiClient.post<StatusCheck>('/status', { client_name: clientName })
      await fetchItems()
    } catch (e: any) {
      setError(e?.message ?? 'Failed to create')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchItems()
  }, [])

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 border-b border-gray-200 bg-white/70 backdrop-blur dark:border-gray-800 dark:bg-gray-950/70">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-3">
            <div className="h-8 w-8 rounded bg-emerald-600" />
            <div>
              <h1 className="text-lg font-semibold">Modern App</h1>
              <p className="text-xs text-gray-500 dark:text-gray-400">Backend: {backendInfo.configuredURL}</p>
            </div>
          </div>
          <button className="btn btn-outline" onClick={() => setDark(!dark)}>
            {dark ? 'Light' : 'Dark'} mode
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-4 py-8">
        <div className="mb-6 grid gap-4 md:grid-cols-2">
          <div className="card">
            <h2 className="mb-2 text-base font-medium">Create Status Check</h2>
            <div className="flex items-center gap-2">
              <input
                className="w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-emerald-500 dark:border-gray-700"
                placeholder="client name"
                value={clientName}
                onChange={(e) => setClientName(e.target.value)}
              />
              <button className="btn btn-primary" disabled={loading} onClick={createStatus}>
                {loading ? 'Saving…' : 'Save'}
              </button>
            </div>
            {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
          </div>

          <div className="card">
            <h2 className="mb-2 text-base font-medium">Health</h2>
            <HealthPanel />
          </div>
        </div>

        <section className="card">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-base font-medium">Recent Status Checks</h2>
            <button className="btn btn-outline" onClick={fetchItems}>Refresh</button>
          </div>
          <ul className="divide-y divide-gray-200 dark:divide-gray-800">
            {items.length === 0 && (
              <li className="py-8 text-center text-sm text-gray-500">No items yet</li>
            )}
            {items.map((it) => (
              <li key={it.id} className="flex items-center justify-between py-3">
                <div>
                  <div className="font-medium">{it.client_name}</div>
                  <div className="text-xs text-gray-500 dark:text-gray-400">
                    {new Date(it.timestamp).toLocaleString()} · {formatDistanceToNow(new Date(it.timestamp), { addSuffix: true })}
                  </div>
                </div>
                <code className="rounded bg-gray-100 px-2 py-1 text-xs dark:bg-gray-800">{it.id}</code>
              </li>
            ))}
          </ul>
        </section>
      </main>
    </div>
  )
}

function HealthPanel() {
  const [data, setData] = useState<{ status: string; mongo: boolean } | null>(null)
  const [err, setErr] = useState<string | null>(null)

  async function load() {
    setErr(null)
    try {
      const res = await apiClient.get('/health')
      setData(res.data)
    } catch (e: any) {
      setErr(e?.message ?? 'Failed to fetch')
    }
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div className="text-sm">
      <div className="flex items-center gap-2">
        <span className="font-medium">Status:</span>
        <span className={data?.status === 'ok' ? 'text-emerald-600' : 'text-red-600'}>
          {data?.status ?? '—'}
        </span>
      </div>
      <div className="mt-1 flex items-center gap-2">
        <span className="font-medium">Mongo:</span>
        <span className={data?.mongo ? 'text-emerald-600' : 'text-red-600'}>
          {data?.mongo === undefined ? '—' : data?.mongo ? 'connected' : 'unavailable'}
        </span>
      </div>
      <button className="btn btn-outline mt-3" onClick={load}>Recheck</button>
      {err && <p className="mt-2 text-red-600">{err}</p>}
    </div>
  )
}