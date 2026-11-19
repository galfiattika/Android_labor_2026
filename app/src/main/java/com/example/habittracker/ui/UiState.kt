package com.example.habittracker.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected fun <T> execute(
        state: MutableLiveData<UiState<T>>,
        action: suspend () -> T
    ) {
        viewModelScope.launch {
            state.value = UiState.Loading
            try {
                state.value = UiState.Success(action())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                state.value = UiState.Error(
                    error.localizedMessage ?: "Something went wrong."
                )
            }
        }
    }

    protected fun <T> clear(state: MutableLiveData<UiState<T>>) {
        state.value = UiState.Idle
    }
}
