package com.example.habittracker.network

import com.example.habittracker.model.AuthRequest
import com.example.habittracker.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/local/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/local/signup")
    suspend fun signUp(@Body request: AuthRequest): Response<AuthResponse>
}
