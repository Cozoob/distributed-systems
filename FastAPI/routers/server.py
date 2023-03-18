from fastapi import APIRouter
from starlette.requests import Request
from starlette.responses import HTMLResponse, Response
from starlette.templating import Jinja2Templates

router = APIRouter()
templates = Jinja2Templates(directory="./websites")


@router.get("/", response_class=HTMLResponse)
async def root(request: Request) -> Response:
    return templates.TemplateResponse("inputForm.html", {"request": request})


@router.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}
