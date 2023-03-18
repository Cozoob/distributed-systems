from functools import lru_cache
from yaml import safe_load
from pydantic import BaseSettings, Field
from typing import Any


class UvicornSettings(BaseSettings):
    """ Setting for uvicorn server """
    host: str
    port: int = Field(ge=1024, le=65535)  # ge-> greater or eq/ le-> less or eq
    reload: bool


class APIConfigSettings(BaseSettings):
    """ Settings for FastAPI server """
    title: str = ""
    docs_url: str


class Settings(BaseSettings):
    uvicorn: UvicornSettings
    api_config: APIConfigSettings


def load_from_yaml() -> Any:
    with open("appsettings.yaml") as fp:
        config = safe_load(fp)
    return config


@lru_cache()
def get_settings() -> Settings:
    yaml_config = load_from_yaml()
    return Settings(**yaml_config)