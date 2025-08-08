from fastapi import FastAPI, APIRouter
from dotenv import load_dotenv
from starlette.middleware.cors import CORSMiddleware
from motor.motor_asyncio import AsyncIOMotorClient
import os
import logging
from pathlib import Path
from pydantic import BaseModel, Field
from typing import List
import uuid
from datetime import datetime


ROOT_DIR = Path(__file__).parent
load_dotenv(ROOT_DIR / ".env")

# MongoDB connection (use safe defaults; connect lazily)
mongo_url = os.getenv("MONGO_URL", "mongodb://localhost:27017")
db_name = os.getenv("DB_NAME", "app")
client = AsyncIOMotorClient(mongo_url)
db = client[db_name]

# Create the main app without a prefix
app = FastAPI()

# Create a router with the /api prefix
api_router = APIRouter(prefix="/api")


# Define Models
class StatusCheck(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    client_name: str
    timestamp: datetime = Field(default_factory=datetime.utcnow)

class StatusCheckCreate(BaseModel):
    client_name: str

# Add your routes to the router instead of directly to app
@api_router.get("/")
async def root():
    return {"message": "Hello World"}

@api_router.post("/status", response_model=StatusCheck)
async def create_status_check(input: StatusCheckCreate):
    status_dict = input.dict()
    status_obj = StatusCheck(**status_dict)
    _ = await db.status_checks.insert_one(status_obj.dict())
    return status_obj

@api_router.get("/status", response_model=List[StatusCheck])
async def get_status_checks():
    # Sort newest first and limit result size; strip Mongo _id field to satisfy the response model
    docs = await db.status_checks.find().sort("timestamp", -1).to_list(100)
    sanitized: List[StatusCheck] = []
    for doc in docs:
        doc.pop("_id", None)
        sanitized.append(StatusCheck(**doc))
    return sanitized

@api_router.get("/health")
async def health_check():
    mongo_ok = True
    try:
        # Motor ping
        await db.command("ping")
    except Exception:
        mongo_ok = False
    return {
        "status": "ok",
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "mongo": mongo_ok,
    }

# Include the router in the main app
app.include_router(api_router)

# CORS configuration: avoid wildcard with credentials
cors_origins_env = os.getenv("CORS_ALLOW_ORIGINS", "*")
if cors_origins_env.strip() == "*":
    allow_origins = ["*"]
    allow_credentials = False
else:
    allow_origins = [origin.strip() for origin in cors_origins_env.split(",") if origin.strip()]
    allow_credentials = os.getenv("CORS_ALLOW_CREDENTIALS", "false").lower() == "true"

app.add_middleware(
    CORSMiddleware,
    allow_origins=allow_origins,
    allow_credentials=allow_credentials,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

@app.on_event("shutdown")
async def shutdown_db_client():
    client.close()
