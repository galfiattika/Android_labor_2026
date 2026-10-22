package com.example.habittracker.repository

import com.example.habittracker.model.AuthRequest
import com.example.habittracker.model.AuthResponse
import com.example.habittracker.network.AuthApiService
import retrofit2.Response

class AuthRepository(
    private val api: AuthApiService
) {
    suspend fun login(email: String, password: String): Response<AuthResponse> =
        api.login(AuthRequest(email = email, password = password))

    suspend fun signUp(email: String, password: String): Response<AuthResponse> =
        api.signUp(AuthRequest(email = email, password = password))
}
