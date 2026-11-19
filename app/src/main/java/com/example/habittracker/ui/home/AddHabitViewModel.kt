package com.example.habittracker.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.CreateHabitRequest
import com.example.habittracker.model.HabitCategory
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

class AddHabitViewModel(application: Application) : BaseViewModel(application) {
    private val repository: HabitRepository =
        (application as HabitTrackerApp).container.habitRepository

    private val _categories = MutableLiveData<UiState<List<HabitCategory>>>(UiState.Idle)
    val categories: LiveData<UiState<List<HabitCategory>>> = _categories
    private val _saveState = MutableLiveData<UiState<HabitResponse>>(UiState.Idle)
    val saveState: LiveData<UiState<HabitResponse>> = _saveState

    fun loadCategories() {
        execute(_categories) { repository.getCategories() }
    }

    fun createHabit(request: CreateHabitRequest) {
        execute(_saveState) { repository.create(request) }
    }

    fun consumeSaveState() = clear(_saveState)
}
