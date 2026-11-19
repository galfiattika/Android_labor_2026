package com.example.habittracker.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.repository.AuthRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

class ResetPasswordViewModel(application: Application) : BaseViewModel(application) {
    private val repository: AuthRepository =
        (application as HabitTrackerApp).container.authRepository

    private val _state = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val state: LiveData<UiState<Unit>> = _state

    fun resetPassword(email: String) {
        execute(_state) { repository.resetPassword(email) }
    }

    fun consumeState() = clear(_state)
}
