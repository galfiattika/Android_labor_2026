package com.example.habittracker.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.model.UpdateScheduleRequest
import com.example.habittracker.repository.ScheduleRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

class EditScheduleViewModel(application: Application) : BaseViewModel(application) {
    private val repository: ScheduleRepository =
        (application as HabitTrackerApp).container.scheduleRepository

    private val _schedule = MutableLiveData<UiState<ScheduleResponse>>(UiState.Idle)
    val schedule: LiveData<UiState<ScheduleResponse>> = _schedule
    private val _saveState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val saveState: LiveData<UiState<Unit>> = _saveState

    fun load(id: Long) {
        execute(_schedule) { repository.getSchedule(id) }
    }

    fun save(id: Long, request: UpdateScheduleRequest) {
        execute(_saveState) {
            repository.update(id, request)
            Unit
        }
    }

    fun consumeSaveState() = clear(_saveState)
}
