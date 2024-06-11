package com.courses.wheatherapp.util

sealed class ErrorHandling<T>(val data:T? = null, val message:String? = null){
    class Loading<T>: ErrorHandling<T>()
    class Success<T>(data: T): ErrorHandling<T>(data)
    class Error<T>(message:String?, data: T? = null): ErrorHandling<T>(data, message)
}