import axios from 'axios'

function ensureTrailingSlash(input: string) {
  return input.endsWith('/') ? input.slice(0, -1) : input
}

const envUrl = import.meta.env.VITE_BACKEND_URL as string | undefined
const baseURL = envUrl ? ensureTrailingSlash(envUrl) + '/api' : `${window.location.origin}/api`

export const apiClient = axios.create({ baseURL })