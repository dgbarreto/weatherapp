package com.avanade.weatherapp.model

import com.google.gson.annotations.SerializedName

data class ForecastDay(
    @SerializedName("date")
    val Date: String,
    @SerializedName("day")
    val Day: Day
)
