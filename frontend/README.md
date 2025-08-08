# Frontend (Vite + React + TypeScript + Tailwind)

## Scripts
- dev: Start dev server on http://localhost:5173
- build: Type-check and build for production
- preview: Preview the production build

## Setup
```bash
cd frontend
# with yarn
corepack enable
yarn install
yarn dev

# or npm
npm install
npm run dev
```

## Environment
- VITE_BACKEND_URL: Optional. If not set, the app will call the backend on same origin (`window.location.origin`). Example:
```bash
# .env.local
VITE_BACKEND_URL=http://localhost:8000
```

## Notes
- Tailwind is configured with `darkMode: 'class'`. The header button toggles dark mode.
- API client is configured in `src/utils/apiClient.ts`.
