package com.example.habittracker.repository

import android.content.Context
import android.net.Uri
import com.example.habittracker.model.ProfileResponse
import com.example.habittracker.model.UpdateProfileRequest
import com.example.habittracker.network.AuthApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepository(
    private val api: AuthApiService,
    private val context: Context
) {
    suspend fun getProfile(): ProfileResponse = api.getProfile().requireBody()

    suspend fun updateProfile(request: UpdateProfileRequest): ProfileResponse =
        api.updateProfile(request).requireBody()

    suspend fun uploadPhoto(uri: Uri): ProfileResponse {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw ApiException("Unable to read selected image.")
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("profileImage", "profile.jpg", body)
        return api.uploadProfileImage(part).requireBody()
    }
}
