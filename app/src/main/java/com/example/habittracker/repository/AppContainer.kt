package com.example.habittracker.repository

import android.content.Context
import com.example.habittracker.network.AuthApiService
import com.example.habittracker.utils.SessionManager

class AppContainer(
    api: AuthApiService,
    context: Context
) {
    val sessionManager = SessionManager(context.applicationContext)
    val authRepository = AuthRepository(api, sessionManager)
    val habitRepository = HabitRepository(api, sessionManager)
    val scheduleRepository = ScheduleRepository(api)
    val profileRepository = ProfileRepository(api, context.applicationContext)
}
