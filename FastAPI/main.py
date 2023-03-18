import uvicorn

from fastapi import FastAPI
from config.settings import get_settings
from routers import server, meteo
from starlette.middleware.cors import CORSMiddleware

api_config = get_settings().api_config

app = FastAPI(
    title=api_config.title,
    docs_url=api_config.docs_url,
)

app.add_middleware(
    CORSMiddleware,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

app.include_router(server.router)
app.include_router(meteo.router)

if __name__ == '__main__':
    settings = get_settings()
    server = settings.uvicorn
    uvicorn.run(
        app="main:app",
        host=server.host,
        port=server.port,
        reload=server.reload
    )
