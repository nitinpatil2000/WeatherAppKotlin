package com.courses.wheatherapp.repository

import com.courses.wheatherapp.api.WeatherApi
import com.courses.wheatherapp.models.Weather
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {

    suspend fun getWeatherData(city:String, appId:String, units:String): Response<Weather> {
        return weatherApi.getWeatherData(city, appId, units)
    }

    suspend fun validateCityName(city: String, appId: String, units: String): Boolean {
        return try {
            val response = weatherApi.getWeatherData(city, appId, units)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}