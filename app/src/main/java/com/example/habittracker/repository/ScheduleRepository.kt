package com.example.habittracker.repository

import android.content.Context
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.network.RetrofitClient

class ScheduleRepository(context: Context) {
    private val api = RetrofitClient.create(context)

    suspend fun getScheduleByDay(day: String): List<ScheduleResponse> {
        return api.getScheduleByDay(day)
    }
}
