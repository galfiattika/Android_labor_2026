package com.example.habittracker

import android.app.Application
import com.example.habittracker.network.RetrofitClient
import com.example.habittracker.repository.AppContainer

class HabitTrackerApp : Application() {
    val container: AppContainer by lazy {
        AppContainer(
            api = RetrofitClient.create(this),
            context = this
        )
    }
}
