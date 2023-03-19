import requests
from fastapi import APIRouter
from config.settings import get_settings
from json import JSONDecodeError
from starlette.templating import Jinja2Templates
from schemas.responses import ForecastExternalResponse

meteo_source_config = get_settings().meteo_source_config
URL = meteo_source_config.url
API_KEY = meteo_source_config.api_key

router = APIRouter()
templates = Jinja2Templates(directory="./websites")
ERROR_PAGE = "error.html"


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
