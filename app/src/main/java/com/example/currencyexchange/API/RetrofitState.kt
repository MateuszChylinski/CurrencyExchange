package com.example.currencyexchange.API

sealed class ApiResult<T>(val data: T? = null, val throwable: String? = null){
      class Success<T>(data: T) : ApiResult<T>(data, null)
      class Error<T>(data: T? = null, error: String? = null): ApiResult<T>(data, error)
}
