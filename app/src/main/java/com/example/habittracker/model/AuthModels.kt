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

data class ProfileResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("profileImageBase64") val profileImageBase64: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("username") val username: String
)

data class CreateHabitRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("goal") val goal: String
)

data class ResetPasswordEmailRequest(val email: String)

data class CreateProgressRequest(
    val scheduleId: Long,
    val date: String,
    val logged_time: Int? = null,
    val notes: String? = null,
    val is_completed: Boolean? = null
)

data class CustomScheduleRequest(
    val habitId: Int,
    val date: String,
    val start_time: String,
    val end_time: String? = null,
    val duration_minutes: Int? = null,
    val is_custom: Boolean = true,
    val participantIds: List<Int>? = null,
    val notes: String? = null
)

data class RecurringScheduleRequest(
    val habitId: Int,
    val start_time: String,
    val end_time: String? = null,
    val duration_minutes: Int? = null,
    val repeatPattern: String,
    val repeatDays: Int = 30,
    val is_custom: Boolean = true,
    val notes: String? = null
)

data class UpdateScheduleRequest(
    val start_time: String? = null,
    val end_time: String? = null,
    val duration_minutes: Int? = null,
    val status: String? = null,
    val date: String? = null,
    val is_custom: Boolean? = null,
    val participantIds: List<Int>? = null,
    val notes: String? = null
)

data class AuthTokens(
    @SerializedName("accessToken")
    val accessToken: String? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null
)
