package com.example.habittracker.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.habittracker.model.AuthResponse
import com.example.habittracker.network.RetrofitClient
import com.example.habittracker.repository.AuthRepository
import com.example.habittracker.utils.SessionManager
import com.google.gson.JsonParseException
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val isLogin: Boolean) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitClient.create(application))
    private val sessionManager = SessionManager(application)

    private val _authState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val authState: LiveData<AuthUiState> = _authState

    fun login(email: String, password: String) {
        authenticate(isLogin = true) {
            repository.login(email, password)
        }
    }

    fun signUp(email: String, password: String, username: String) {
        authenticate(isLogin = false) {
            repository.signUp(email, password, username)
        }
    }

    fun consumeState() {
        _authState.value = AuthUiState.Idle
    }

    private fun authenticate(
        isLogin: Boolean,
        request: suspend () -> Response<AuthResponse>
    ) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val response = request()
                if (!response.isSuccessful) {
                    _authState.value = AuthUiState.Error(
                        "Request failed (HTTP ${response.code()})."
                    )
                    return@launch
                }

                val authResponse = response.body()
                if (isLogin) {
                    val token = authResponse?.tokens?.accessToken
                    if (token.isNullOrBlank()) {
                        _authState.value = AuthUiState.Error(
                            "The server response did not include an access token."
                        )
                        return@launch
                    }
                    sessionManager.saveAuthToken(token)
                }
                _authState.value = AuthUiState.Success(isLogin)
            } catch (error: IOException) {
                _authState.value = AuthUiState.Error(
                    error.localizedMessage ?: "Unable to reach the server. Check your connection."
                )
            } catch (error: JsonParseException) {
                _authState.value = AuthUiState.Error(
                    "The server returned an invalid response."
                )
            }
        }
    }
}
