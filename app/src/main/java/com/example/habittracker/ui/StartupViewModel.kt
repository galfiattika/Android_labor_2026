package com.example.habittracker.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.habittracker.HabitTrackerApp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartupViewModel(application: Application) : BaseViewModel(application) {
    private val container = (application as HabitTrackerApp).container
    private val _destination = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val destination: LiveData<UiState<Boolean>> = _destination

    fun start() {
        viewModelScope.launch {
            _destination.value = UiState.Loading
            delay(800)
            val hasSession = try {
                container.authRepository.restoreSession()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                container.authRepository.clearSession()
                false
            }
            _destination.value = UiState.Success(hasSession)
        }
    }

    fun consumeDestination() = clear(_destination)
}
