package com.example.habittracker.repository

import com.example.habittracker.model.AuthRequest
import com.example.habittracker.model.AuthResponse
import com.example.habittracker.network.AuthApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class AuthRepository(
    private val api: AuthApiService
) {
    suspend fun login(email: String, password: String): Response<AuthResponse> =
        api.login(AuthRequest(email = email, password = password))

    suspend fun signUp(email: String, password: String, username: String): Response<AuthResponse> {
        // A szöveges mezőket RequestBody-vá alakítjuk a multipart kéréshez
        val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

        return api.signUp(
            username = usernameBody,
            email = emailBody,
            password = passwordBody,
            profileImage = null // Opcionális kép, ha most nem küldünk
        )
    }
}