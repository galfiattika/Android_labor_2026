package com.example.habittracker.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.CreateProgressRequest
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.model.UpdateScheduleRequest
import com.example.habittracker.repository.ScheduleRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

enum class ScheduleAction { PROGRESS_ADDED, NOTES_UPDATED, DELETED }

class ScheduleDetailsViewModel(application: Application) : BaseViewModel(application) {
    private val repository: ScheduleRepository =
        (application as HabitTrackerApp).container.scheduleRepository

    private val _schedule = MutableLiveData<UiState<ScheduleResponse>>(UiState.Idle)
    val schedule: LiveData<UiState<ScheduleResponse>> = _schedule
    private val _actionState = MutableLiveData<UiState<ScheduleAction>>(UiState.Idle)
    val actionState: LiveData<UiState<ScheduleAction>> = _actionState

    fun load(id: Long) {
        execute(_schedule) { repository.getSchedule(id) }
    }

    fun addProgress(request: CreateProgressRequest) {
        execute(_actionState) {
            repository.addProgress(request)
            ScheduleAction.PROGRESS_ADDED
        }
    }

    fun updateNotes(id: Long, notes: String) {
        execute(_actionState) {
            repository.update(id, UpdateScheduleRequest(notes = notes))
            ScheduleAction.NOTES_UPDATED
        }
    }

    fun delete(id: Long) {
        execute(_actionState) {
            repository.delete(id)
            ScheduleAction.DELETED
        }
    }

    fun consumeActionState() = clear(_actionState)
}
