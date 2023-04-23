package com.umsl.kma9q7.weatherapp

data class Weather(
    val results: List<WeatherResult>
)

data class WeatherResult (
    val temperature: Double
)