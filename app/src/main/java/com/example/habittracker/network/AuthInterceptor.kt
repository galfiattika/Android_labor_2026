package com.example.habittracker.network

import com.example.habittracker.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            sessionManager.fetchAuthToken()?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}
