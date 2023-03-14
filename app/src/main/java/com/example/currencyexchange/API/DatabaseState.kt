package com.example.currencyexchange.API

sealed class DatabaseState<T>(val data: T? = null, val message: String? = null) {
     class Success<T>(data: T) : DatabaseState<T>(data, null)
     class Error<T>(data: T? = null, error: String? = null) : DatabaseState<T>(data, error)
}
