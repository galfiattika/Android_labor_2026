package com.example.habittracker.repository

import com.example.habittracker.model.CreateProgressRequest
import com.example.habittracker.model.CustomScheduleRequest
import com.example.habittracker.model.RecurringScheduleRequest
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.model.UpdateScheduleRequest
import com.example.habittracker.network.AuthApiService

class ScheduleRepository(private val api: AuthApiService) {

    suspend fun getScheduleByDay(day: String): List<ScheduleResponse> {
        return api.getScheduleByDay(day)
    }

    suspend fun getSchedules(): List<ScheduleResponse> = api.getSchedules().requireBody()

    suspend fun getSchedule(id: Long): ScheduleResponse = api.getSchedule(id).requireBody()

    suspend fun createCustom(request: CustomScheduleRequest): ScheduleResponse =
        api.createCustomSchedule(request).requireBody()

    suspend fun createRecurring(request: RecurringScheduleRequest): List<ScheduleResponse> =
        api.createRecurringSchedules(request).requireBody()

    suspend fun update(id: Long, request: UpdateScheduleRequest): ScheduleResponse =
        api.updateSchedule(id, request).requireBody()

    suspend fun delete(id: Long) = api.deleteSchedule(id).requireSuccess()

    suspend fun addProgress(request: CreateProgressRequest) =
        api.createProgress(request).requireBody()
}
