import requests
from fastapi import APIRouter
from config.settings import get_settings
from json import JSONDecodeError
from schemas.responses import ForecastExternalResponse
from starlette.responses import JSONResponse

m3o_source_config = get_settings().m3o_source_config
URL = m3o_source_config.url
API_KEY = m3o_source_config.api_key

router = APIRouter()


class M3OForecast:

    def get_forecast(self, city_name: str, days: int) -> dict:
        response = requests.post(
            URL,
            json={
                "location": city_name,
                "days": days
            },
            headers={"Authorization": f'Bearer {API_KEY}'}
        )

        forecast_val = {"is_error": False, "details": {}}
        response = response.json()

        if response["code"] != 200:
            forecast_val["is_error"] = True
            forecast_val["details"]["message"] = "Error while connecting to external API. Contact your administrator."
            print("Status code when finding city by name: ", response["code"])
            try:
                print(response.json())
            except JSONDecodeError:
                print(response.content)

            return forecast_val

        daily_forecast = response["forecast"]
        forecast_val["details"]["value"] = []
        results = forecast_val["details"]["value"]

        for val in daily_forecast:
            results.append(
                ForecastExternalResponse(
                    val["date"],
                    val["min_temp_c"],
                    val["max_temp_c"],
                    val["avg_temp_c"],
                    val["condition"],
                    val["max_wind_kph"]
                )
            )

        return forecast_val


@router.post("/m3o/{city_name}")
async def get_forecast_by_city_name(city_name: str, days: int):
    response = requests.post(
        URL,
        json={
            "location": city_name,
            "days": days
        },
        headers={"Authorization": f'Bearer {API_KEY}'}
    ).json()

    if response["code"] != 200:
        return JSONResponse(status_code=response["code"], content=response)

    return response
