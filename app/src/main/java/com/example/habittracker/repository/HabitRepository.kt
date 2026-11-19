package com.example.habittracker.repository

import com.example.habittracker.model.CreateHabitRequest
import com.example.habittracker.model.HabitCategory
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.network.AuthApiService
import com.example.habittracker.utils.SessionManager

class HabitRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    fun currentUserId(): Int? = sessionManager.fetchUserId()

    suspend fun getMyHabits(): List<HabitResponse> {
        val userId = currentUserId() ?: throw ApiException("Please sign in again.")
        return api.getUserHabits(userId).requireBody()
    }

    suspend fun getCategories(): List<HabitCategory> =
        api.getHabitCategories().requireBody()

    suspend fun create(request: CreateHabitRequest): HabitResponse =
        api.createHabit(request).requireBody()
}
