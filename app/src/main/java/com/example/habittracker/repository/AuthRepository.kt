package com.example.habittracker.repository

import com.example.habittracker.model.AuthRequest
import com.example.habittracker.model.AuthResponse
import com.example.habittracker.model.AuthTokens
import com.example.habittracker.model.ResetPasswordEmailRequest
import com.example.habittracker.network.AuthApiService
import com.example.habittracker.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): AuthResponse {
        val result = api.login(AuthRequest(email = email, password = password)).requireBody()
        saveSession(result, requireAccessToken = true)
        return result
    }

    suspend fun signUp(email: String, password: String, username: String): AuthResponse {
        val result = api.signUp(
            username = username.toRequestBody("text/plain".toMediaTypeOrNull()),
            email = email.toRequestBody("text/plain".toMediaTypeOrNull()),
            password = password.toRequestBody("text/plain".toMediaTypeOrNull())
        ).requireBody()
        saveSession(result, requireAccessToken = false)
        return result
    }

    suspend fun resetPassword(email: String) {
        api.resetPasswordViaEmail(ResetPasswordEmailRequest(email)).requireSuccess()
    }

    suspend fun logout() {
        api.logout().requireSuccess()
        sessionManager.clearAuthToken()
    }

    suspend fun refreshSession(): Boolean {
        val refreshToken = sessionManager.fetchRefreshToken() ?: return false
        val tokens = api.refreshTokens("Bearer $refreshToken").requireBody()
        saveTokens(tokens, requireAccessToken = true)
        return true
    }

    suspend fun restoreSession(): Boolean =
        if (sessionManager.fetchRefreshToken().isNullOrBlank()) hasAccessToken()
        else refreshSession()

    fun clearSession() {
        sessionManager.clearAuthToken()
    }

    fun hasAccessToken(): Boolean = !sessionManager.fetchAuthToken().isNullOrBlank()

    private fun saveSession(response: AuthResponse, requireAccessToken: Boolean) {
        val tokens = response.tokens
        if (tokens != null) saveTokens(tokens, requireAccessToken)
        else if (requireAccessToken) {
            throw ApiException("The server response did not include authentication tokens.")
        }
        response.user?.id?.let(sessionManager::saveUserId)
    }

    private fun saveTokens(tokens: AuthTokens, requireAccessToken: Boolean) {
        val accessToken = tokens.accessToken
        if (accessToken.isNullOrBlank()) {
            if (requireAccessToken) {
                throw ApiException("The server response did not include an access token.")
            }
            return
        }
        sessionManager.saveAuthToken(accessToken)
        tokens.refreshToken?.let(sessionManager::saveRefreshToken)
    }
}
