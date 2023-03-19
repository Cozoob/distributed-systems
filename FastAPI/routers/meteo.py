import requests
from fastapi import APIRouter
from config.settings import get_settings

meteo_source_config = get_settings().meteo_source_config
URL = meteo_source_config.url
API_KEY = meteo_source_config.api_key

router = APIRouter()


# async def request(session: aiohttp.ClientSession) -> str:
#     async with session.get(URL) as response:
#         return await response.text()
#
#
# async def task():
#     async with aiohttp.ClientSession() as session:
#         tasks = [request(session) for _ in range(100)]
#         return await asyncio.gather(*tasks)
#
#
# @router.get("/uuids")
# async def get_uuids():
#     return {"uuids": await task()}

# @router.get("/weather")
# async def submit_form():
#
class MeteoWeather:

    def get_forecast(self, city_name: str, days: int):
        ...


@router.get("/meteo/{city}")
async def get_london(city: str):
    params = {
        'key': API_KEY,
        'place_id': 'london'
    }
    return requests.get(URL, params).json()
