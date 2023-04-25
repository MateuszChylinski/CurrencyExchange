package com.example.currencyexchange.DataWrapper

sealed class DataWrapper<T>(val data: T? = null, val message: String? = null) {
     class Success<T>(data: T) : DataWrapper<T>(data, null)
     class Error<T>(data: T?, error: String? = null) : DataWrapper<T>(data, error)
}
