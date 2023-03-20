import uvicorn

from fastapi import FastAPI
from config.settings import get_settings
from routers import server, meteo, m3o, uuid
from starlette.middleware.cors import CORSMiddleware

routers_to_include = [server.router, meteo.router, m3o.router, uuid.router]

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

for router in routers_to_include:
    app.include_router(router)

if __name__ == '__main__':
    settings = get_settings()
    server = settings.uvicorn
    uvicorn.run(
        app="main:app",
        host=server.host,
        port=server.port,
        reload=server.reload
    )
