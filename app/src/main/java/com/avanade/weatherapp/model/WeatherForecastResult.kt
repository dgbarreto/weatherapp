package com.avanade.weatherapp.model

import com.google.gson.annotations.SerializedName

data class WeatherForecastResult(
    @SerializedName("forecast")
    var forecast: Forecast
)
