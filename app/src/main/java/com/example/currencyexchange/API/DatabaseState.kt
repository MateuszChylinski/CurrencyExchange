package com.example.currencyexchange.API

sealed class DatabaseState {
    data class Success<out T>(val data: T) : DatabaseState()
    data class Error<out T>(val error: T) : DatabaseState()

}
