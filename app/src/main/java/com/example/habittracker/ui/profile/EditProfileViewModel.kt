package com.example.habittracker.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.ProfileResponse
import com.example.habittracker.model.UpdateProfileRequest
import com.example.habittracker.repository.ProfileRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState

data class EditProfileActionResult(
    val profile: ProfileResponse,
    val photoUploaded: Boolean
)

class EditProfileViewModel(application: Application) : BaseViewModel(application) {
    private val repository: ProfileRepository =
        (application as HabitTrackerApp).container.profileRepository

    private val _profile = MutableLiveData<UiState<ProfileResponse>>(UiState.Idle)
    val profile: LiveData<UiState<ProfileResponse>> = _profile
    private val _actionState = MutableLiveData<UiState<EditProfileActionResult>>(UiState.Idle)
    val actionState: LiveData<UiState<EditProfileActionResult>> = _actionState

    fun load() {
        execute(_profile) { repository.getProfile() }
    }

    fun updateUsername(username: String) {
        execute(_actionState) {
            EditProfileActionResult(
                profile = repository.updateProfile(UpdateProfileRequest(username)),
                photoUploaded = false
            )
        }
    }

    fun uploadPhoto(uri: Uri) {
        execute(_actionState) {
            EditProfileActionResult(repository.uploadPhoto(uri), photoUploaded = true)
        }
    }

    fun consumeActionState() = clear(_actionState)
}
