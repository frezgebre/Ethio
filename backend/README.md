# Backend (FastAPI)

## Run locally
```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn server:app --reload --port 8000
```

Env vars:
- MONGO_URL: default `mongodb://localhost:27017`
- DB_NAME: default `app`
- CORS_ALLOW_ORIGINS: `*` by default (no credentials)

## Docker
```bash
# from repo root
docker compose up --build
# backend: http://localhost:8000
# health:  http://localhost:8000/api/health
```