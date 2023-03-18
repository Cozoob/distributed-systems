from fastapi import APIRouter
from starlette.requests import Request
from starlette.responses import HTMLResponse, Response
from starlette.templating import Jinja2Templates
from starlette.datastructures import URL

router = APIRouter()
templates = Jinja2Templates(directory="./websites")


@router.get("/", response_class=HTMLResponse)
async def root(request: Request) -> Response:
    return templates.TemplateResponse("inputForm.html", {"request": request})


@router.get("/weather")
async def submit_form(request: Request, city: str):

    if not city.isalpha():
        # check if contains only letters
        return templates.TemplateResponse("error.html", {"request": request})

    return templates.TemplateResponse("city.html", {"request": request, "val": "suprise"})

    # if not city:
    #     return templates.TemplateResponse("inputForm.html", {"message": "error"})
    #
    # return {"chosen city": city}
