package com.example.habittracker.network

import com.example.habittracker.model.AuthRequest
import com.example.habittracker.model.AuthResponse
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.model.ProfileResponse
import com.example.habittracker.model.UpdateProfileRequest
import com.example.habittracker.model.CreateHabitRequest
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.model.HabitCategory
import com.example.habittracker.model.ResetPasswordEmailRequest
import com.example.habittracker.model.CreateProgressRequest
import com.example.habittracker.model.CustomScheduleRequest
import com.example.habittracker.model.UpdateScheduleRequest
import com.example.habittracker.model.AuthTokens
import com.example.habittracker.model.RecurringScheduleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.Header

interface AuthApiService {
    @POST("/auth/local/signin")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @Multipart
    @POST("/auth/local/signup")
    suspend fun signUp(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profileImage: MultipartBody.Part? = null
    ): Response<AuthResponse>

    @GET("/schedule/day")
    suspend fun getScheduleByDay(@Query("date") day: String): List<ScheduleResponse>

    @GET("/schedule")
    suspend fun getSchedules(): Response<List<ScheduleResponse>>

    @GET("/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @GET("/habit/user/{userId}")
    suspend fun getUserHabits(@Path("userId") userId: Int): Response<List<HabitResponse>>

    @POST("/habit")
    suspend fun createHabit(@Body request: CreateHabitRequest): Response<HabitResponse>

    @GET("/habit/categories")
    suspend fun getHabitCategories(): Response<List<HabitCategory>>

    @PATCH("/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>

    @Multipart
    @POST("/profile/upload-profile-image")
    suspend fun uploadProfileImage(
        @Part profileImage: MultipartBody.Part
    ): Response<ProfileResponse>

    @POST("/auth/local/logout")
    suspend fun logout(): Response<Unit>

    @POST("/auth/reset-password-via-email")
    suspend fun resetPasswordViaEmail(@Body request: ResetPasswordEmailRequest): Response<Unit>

    @POST("/auth/local/refresh")
    suspend fun refreshTokens(@Header("Authorization") authorization: String): Response<AuthTokens>

    @POST("/schedule/custom")
    suspend fun createCustomSchedule(@Body request: CustomScheduleRequest): Response<ScheduleResponse>

    @POST("/schedule/recurring")
    suspend fun createRecurringSchedules(@Body request: RecurringScheduleRequest): Response<List<ScheduleResponse>>

    @GET("/schedule/{id}")
    suspend fun getSchedule(@Path("id") id: Long): Response<ScheduleResponse>

    @PATCH("/schedule/{id}")
    suspend fun updateSchedule(@Path("id") id: Long, @Body request: UpdateScheduleRequest): Response<ScheduleResponse>

    @DELETE("/schedule/{id}")
    suspend fun deleteSchedule(@Path("id") id: Long): Response<Unit>

    @POST("/progress")
    suspend fun createProgress(@Body request: CreateProgressRequest): Response<com.example.habittracker.model.ProgressResponseDto>
}