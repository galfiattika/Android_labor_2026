package com.example.habittracker.ui.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.model.ProfileResponse
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.repository.ProfileRepository
import com.example.habittracker.repository.ScheduleRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

data class ProfileScreenData(
    val profile: ProfileResponse,
    val habits: List<HabitResponse>,
    val schedules: List<ScheduleResponse>
)

class ProfileViewModel(application: Application) : BaseViewModel(application) {
    private val container = (application as HabitTrackerApp).container
    private val profiles: ProfileRepository = container.profileRepository
    private val habits: HabitRepository = container.habitRepository
    private val schedules: ScheduleRepository = container.scheduleRepository
    private val auth = container.authRepository

    private val _profile = MutableLiveData<UiState<ProfileScreenData>>(UiState.Idle)
    val profile: LiveData<UiState<ProfileScreenData>> = _profile
    private val _logoutState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val logoutState: LiveData<UiState<Unit>> = _logoutState

    fun load() {
        execute(_profile) {
            ProfileScreenData(
                profile = profiles.getProfile(),
                habits = habits.getMyHabits(),
                schedules = schedules.getSchedules()
            )
        }
    }

    fun logout() {
        execute(_logoutState) { auth.logout() }
    }

    fun consumeLogoutState() = clear(_logoutState)
}
