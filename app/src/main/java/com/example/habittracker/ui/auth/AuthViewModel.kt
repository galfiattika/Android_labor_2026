package com.example.habittracker.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.repository.AuthRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

class AuthViewModel(application: Application) : BaseViewModel(application) {
    private val repository: AuthRepository =
        (application as HabitTrackerApp).container.authRepository

    private val _authState = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val authState: LiveData<UiState<Boolean>> = _authState

    fun login(email: String, password: String) {
        execute(_authState) {
            repository.login(email, password)
            true
        }
    }

    fun signUp(email: String, password: String, username: String) {
        execute(_authState) {
            repository.signUp(email, password, username)
            false
        }
    }

    fun consumeState() = clear(_authState)
}
