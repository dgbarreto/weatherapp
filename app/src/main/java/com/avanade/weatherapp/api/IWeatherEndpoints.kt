package com.avanade.weatherapp.api

import com.avanade.weatherapp.model.WeatherForecastResult
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface IWeatherEndpoints {
    @Headers(
        "X-RapidAPI-Host: weatherapi-com.p.rapidapi.com",
        "X-RapidAPI-Key: edcb6e1fb1mshfce00c400984ceep107ed7jsnf6cef0b7a37c"
    )
    @GET("forecast.json")
    fun requestWeatherForecast(@Query("q") city: String, @Query("days") numberOfDays: Int) : Maybe<WeatherForecastResult>
}