package com.courses.wheatherapp.viewmodel

import android.content.pm.ApplicationInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.courses.wheatherapp.models.Weather
import com.courses.wheatherapp.repository.WeatherRepository
import com.courses.wheatherapp.util.Constant.WEATHER_API_KEY
import com.courses.wheatherapp.util.ErrorHandling
import com.github.ybq.android.spinkit.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<ErrorHandling<Weather>>()
    val weatherData: LiveData<ErrorHandling<Weather>> get() = _weatherData


    fun fetchWeatherData(city: String, units: String) {
        viewModelScope.launch {
            _weatherData.postValue(ErrorHandling.Loading())

           try {
               val response = repository.getWeatherData(city, WEATHER_API_KEY, units)
               if (response.isSuccessful) {
                   response.body()?.let {
                       _weatherData.postValue(ErrorHandling.Success(it))
                   } ?: run {
                       _weatherData.postValue(ErrorHandling.Error("No data found"))
                   }
               }else{
                   _weatherData.postValue(ErrorHandling.Error("Error: ${response.message()}"))
               }
           } catch (e: Exception) {
               _weatherData.postValue(ErrorHandling.Error(e.message))
           }
        }
    }

    fun validateCityName(city: String, units: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val isValid = repository.validateCityName(city, WEATHER_API_KEY, units)
            result.postValue(isValid)
        }
        return result
    }


}