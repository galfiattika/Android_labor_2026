package com.example.habittracker.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.CustomScheduleRequest
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.model.RecurringScheduleRequest
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.repository.ScheduleRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

class CreateScheduleViewModel(application: Application) : BaseViewModel(application) {
    private val container = (application as HabitTrackerApp).container
    private val habitsRepository: HabitRepository = container.habitRepository
    private val schedulesRepository: ScheduleRepository = container.scheduleRepository

    private val _habits = MutableLiveData<UiState<List<HabitResponse>>>(UiState.Idle)
    val habits: LiveData<UiState<List<HabitResponse>>> = _habits
    private val _saveState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val saveState: LiveData<UiState<Unit>> = _saveState

    fun loadHabits() {
        execute(_habits) { habitsRepository.getMyHabits() }
    }

    fun createCustom(request: CustomScheduleRequest) {
        execute(_saveState) {
            schedulesRepository.createCustom(request)
            Unit
        }
    }

    fun createRecurring(request: RecurringScheduleRequest) {
        execute(_saveState) {
            schedulesRepository.createRecurring(request)
            Unit
        }
    }

    fun consumeSaveState() = clear(_saveState)
}
