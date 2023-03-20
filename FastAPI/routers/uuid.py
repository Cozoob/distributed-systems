import requests
from fastapi import APIRouter
from config.settings import get_settings, USERS_API_KEYS
from starlette.responses import JSONResponse
from starlette import status

uuid_source_config = get_settings().uuid_source_config
URL = uuid_source_config.url

router = APIRouter()


@router.get("/register")
async def get_new_API_key():
    response = requests.get(URL).json()

    if not response["uuid"]:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content={
            "code": status.HTTP_404_NOT_FOUND,
            "message": "Cannot find uuid"
        })

    USERS_API_KEYS.append(response["uuid"])
    return JSONResponse(status_code=status.HTTP_201_CREATED, content={"API_KEY": response["uuid"]})
