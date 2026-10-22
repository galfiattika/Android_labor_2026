package com.example.habittracker.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("user")
    val user: AuthUser? = null,
    @SerializedName("tokens")
    val tokens: AuthTokens? = null
)

data class AuthUser(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("auth_provider")
    val authProvider: String? = null,
    @SerializedName("profile")
    val profile: UserProfile? = null
)

data class UserProfile(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerializedName("profileImageBase64")
    val profileImageBase64: String? = null,
    @SerializedName("coverImageUrl")
    val coverImageUrl: String? = null,
    @SerializedName("fcmToken")
    val fcmToken: String? = null,
    @SerializedName("preferences")
    val preferences: Map<String, Any>? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class AuthTokens(
    @SerializedName("accessToken")
    val accessToken: String? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null
)
