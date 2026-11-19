package com.example.habittracker.repository

import retrofit2.Response

internal fun <T : Any> Response<T>.requireBody(): T {
    if (!isSuccessful) {
        throw ApiException("Request failed (HTTP ${code()}).")
    }
    return body() ?: throw ApiException("The server returned an empty response.")
}

internal fun Response<*>.requireSuccess() {
    if (!isSuccessful) {
        throw ApiException("Request failed (HTTP ${code()}).")
    }
}

class ApiException(message: String) : Exception(message)
