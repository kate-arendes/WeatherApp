package com.umsl.kma9q7.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OwEndpoints {

    @GET("weather?")
    fun getWeather(@Query("lat") latitude: String, @Query("lon") longitude: String, @Query("appid") key: String, @Query("units") units : String):
            Call<WeatherData>

}