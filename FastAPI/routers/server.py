from fastapi import APIRouter
from starlette.requests import Request
from starlette.responses import HTMLResponse, Response
from starlette.templating import Jinja2Templates
from routers.meteo import MeteoForecast
from routers.m3o import M3OForecast
from schemas.responses import WeatherResponse

router = APIRouter()
templates = Jinja2Templates(directory="./websites")
ERROR_PAGE = "error.html"
EXTERNAL_SERVICES = [MeteoForecast(), M3OForecast()]


@router.get("/", response_class=HTMLResponse)
async def root(request: Request) -> Response:
    return templates.TemplateResponse("inputForm.html", {"request": request})


@router.get("/weather", response_class=HTMLResponse)
async def submit_form(request: Request, city: str, days: int):
    global summary
    if not city.isalpha():
        # check if contains only letters
        return templates.TemplateResponse(ERROR_PAGE, {
            "request": request,
            "message": "The city's name is incorrect!"
        })

    if not (1 <= days <= 5):
        # incorrect days param
        return templates.TemplateResponse(ERROR_PAGE, {
            "request": request,
            "message": "The number of days is incorrect!"
        })

    # get forecast and return information
    fetched_data = {}
    for service in EXTERNAL_SERVICES:
        forecast_val = service.get_forecast(city_name=city, days=days)

        if forecast_val["is_error"]:
            # show error page with error message
            return templates.TemplateResponse(ERROR_PAGE, {
                "request": request,
                "message": forecast_val["details"]["message"]
            })

        results = forecast_val["details"]["value"]

        # TODO sprawdz czy daty z drugiego serwisu sa takie same!
        for res in results:
            info = fetched_data.get(res.date, [])
            info.append(res)
            fetched_data[res.date] = info

    results = {}
    for date, info in fetched_data.items():
        amount_of_data = len(info)

        # averages
        avg_temp = round(sum(map(lambda response: response.temp, info)) / amount_of_data, 2)
        avg_min_temp = round(sum(map(lambda response: response.temp_min, info)) / amount_of_data, 2)
        avg_max_temp = round(sum(map(lambda response: response.temp_max, info)) / amount_of_data, 2)
        avg_wind_speed = round(sum(map(lambda response: response.wind_speed, info)) / amount_of_data, 2)

        # variances
        var_temp = round(sum(map(lambda response: (response.temp - avg_temp) ** 2, info)) / amount_of_data, 5)
        var_min_temp = round(sum(map(lambda response: (response.temp_min - avg_min_temp) ** 2, info)) / amount_of_data, 5)
        var_max_temp = round(sum(map(lambda response: (response.temp_max - avg_max_temp) ** 2, info)) / amount_of_data, 5)
        var_wind_speed = round(sum(map(lambda response: (response.wind_speed - avg_wind_speed) ** 2, info)) / amount_of_data, 5)

        for response in info:
            if response.summary:
                summary = response.summary
                break
            else:
                summary = "Summary not found"

        res = results.get(date, [])
        res.append(
            WeatherResponse(
                city=city,
                date=date,
                avg_temp_min=avg_min_temp,
                var_temp_min=var_min_temp,
                avg_temp_max=avg_max_temp,
                var_temp_max=var_max_temp,
                avg_temp=avg_temp,
                var_temp=var_temp,
                summary=summary,
                avg_wind_speed=avg_wind_speed,
                var_wind_speed=var_wind_speed
            )
        )
        results[date] = res
    print(results)
    return templates.TemplateResponse("city.html", {"request": request, "data": results, "city": city})
