import requests
from fastapi import APIRouter
from config.settings import get_settings, USERS_API_KEYS
from json import JSONDecodeError
from schemas.responses import ForecastExternalResponse
from starlette.responses import JSONResponse
from starlette import status

meteo_source_config = get_settings().meteo_source_config
URL = meteo_source_config.url
API_KEY = meteo_source_config.api_key

router = APIRouter()


class MeteoForecast:

    def get_forecast(self, city_name: str, days: int) -> dict:
        # find the city id
        response = requests.get(
            URL + "/find_places",
            params={
                'key': API_KEY,
                'language': 'en',
                'text': city_name
            }
        )

        forecast_val = {"is_error": False, "details": {}}

        if response.status_code != 200:
            forecast_val["is_error"] = True
            forecast_val["details"]["message"] = "Error while connecting to external API. Contact your administrator."
            print("Status code when finding city by name: ", response.status_code)
            try:
                print(response.json())
            except JSONDecodeError:
                print(response.content)

            return forecast_val

        found_cities = response.json()

        if not found_cities:
            forecast_val["is_error"] = True
            forecast_val["details"]["message"] = "Error while finding your city. It may not exist."
            return forecast_val

        city_id = found_cities[0]["place_id"]

        # check the city's forecast
        response = requests.get(
            URL + "/point",
            params={
                'key': API_KEY,
                'language': 'en',
                'place_id': city_id,
                "sections": 'daily',
                "units": "metric"
            }
        )

        if response.status_code != 200:
            forecast_val["is_error"] = True
            forecast_val["details"]["message"] = "Error while connecting to external API. Contact your administrator."
            print("Status code when finding city by name: ", response.status_code)
            try:
                print(response.json())
            except JSONDecodeError:
                print(response.content)

            return forecast_val

        daily_forecast = response.json()["daily"]["data"][:days]
        forecast_val["details"]["value"] = []
        results = forecast_val["details"]["value"]

        for val in daily_forecast:
            results.append(
                ForecastExternalResponse(
                    val["day"],
                    val["all_day"]["temperature_min"],
                    val["all_day"]["temperature_max"],
                    val["all_day"]["temperature"],
                    val["summary"],
                    val["all_day"]["wind"]["speed"]
                )
            )

        return forecast_val


@router.get("/meteo/{city_name}")
async def get_city_id(city_name: str, days: int, api_key: str):
    if api_key not in USERS_API_KEYS:
        return JSONResponse(status_code=status.HTTP_403_FORBIDDEN, content={"message": "Invalid API Key"})

    if not (1 <= days <= 5):
        return JSONResponse(status_code=status.HTTP_406_NOT_ACCEPTABLE, content={
            "code": status.HTTP_406_NOT_ACCEPTABLE,
            "message": "Days parameter must be number and be between 1 and 5"
        })

    response = requests.get(
        URL + "/find_places",
        params={
            'key': API_KEY,
            'language': 'en',
            'text': city_name
        }
    )

    if response.status_code != 200:
        return JSONResponse(status_code=response.status_code, content=response.json())

    results = response.json()

    if not results:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content={
            "code": status.HTTP_404_NOT_FOUND,
            "message": "City was not found"
        })

    city_id = results[0]["place_id"]

    response = requests.get(
        URL + "/point",
        params={
            'key': API_KEY,
            'language': 'en',
            'place_id': city_id,
            "sections": 'daily',
            "units": "metric"
        }
    )

    if response.status_code != 200:
        return JSONResponse(status_code=response.status_code, content=response.json())

    return response.json()
