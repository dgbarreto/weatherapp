package com.avanade.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("forecastday")
    var ForecastDay: List<ForecastDay>
)
