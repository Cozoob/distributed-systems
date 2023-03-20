from typing import Union


class ForecastExternalResponse:
    def __init__(self, date: str, temp_min: int, temp_max: int, temp: int, summary: Union[str, None],
                 wind_speed: int):
        self.date = date
        self.temp_min = temp_min
        self.temp_max = temp_max
        self.temp = temp
        self.summary = summary
        self.wind_speed = wind_speed


class WeatherResponse:
    def __init__(self, city: str, date: str, avg_temp_min: int, var_temp_min: int, avg_temp_max: int, var_temp_max: int,
                 avg_temp: int, var_temp: int, summary: Union[str, None], avg_wind_speed: int, var_wind_speed: int):
        self.city = city
        self.date = date
        self.avg_temp_min = avg_temp_min
        self.var_temp_min = var_temp_min
        self.avg_temp_max = avg_temp_max
        self.var_temp_max = var_temp_max
        self.avg_temp = avg_temp
        self.var_temp = var_temp
        self.summary = summary
        self.avg_wind_speed = avg_wind_speed
        self.var_wind_speed = var_wind_speed
