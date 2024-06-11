package com.courses.wheatherapp.di

import com.courses.wheatherapp.api.WeatherApi
import com.courses.wheatherapp.util.Constant.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideWeatherApi(retrofitBuilder: Retrofit.Builder): WeatherApi {
        return retrofitBuilder.build().create(WeatherApi::class.java)
    }
}