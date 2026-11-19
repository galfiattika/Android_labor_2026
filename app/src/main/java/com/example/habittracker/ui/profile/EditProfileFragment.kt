package com.example.habittracker.ui.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.databinding.FragmentEditProfileBinding
import com.example.habittracker.ui.UiState

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()

    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let(viewModel::uploadPhoto)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnChoosePhoto.setOnClickListener { photoPicker.launch("image/*") }
        binding.btnSaveProfile.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            if (username.isBlank()) {
                Toast.makeText(requireContext(), "Username is required.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateUsername(username)
            }
        }
        viewModel.profile.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                is UiState.Success -> {
                    binding.etUsername.setText(state.data.username.orEmpty())
                    binding.tvEmail.text = state.data.email.orEmpty()
                    state.data.profileImageBase64?.let(::showEncodedPhoto)
                }
            }
        }
        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            binding.btnSaveProfile.isEnabled = state !is UiState.Loading
            binding.btnChoosePhoto.isEnabled = state !is UiState.Loading
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeActionState()
                }
                is UiState.Success -> {
                    state.data.profile.profileImageBase64?.let(::showEncodedPhoto)
                    if (state.data.photoUploaded) {
                        Toast.makeText(requireContext(), "Profile photo uploaded.", Toast.LENGTH_SHORT).show()
                        binding.etUsername.setText(state.data.profile.username.orEmpty())
                        binding.tvEmail.text = state.data.profile.email.orEmpty()
                        viewModel.consumeActionState()
                    } else {
                        Toast.makeText(requireContext(), "Profile updated.", Toast.LENGTH_SHORT).show()
                        viewModel.consumeActionState()
                        findNavController().navigateUp()
                    }
                }
            }
        }
        viewModel.load()
    }

    private fun showEncodedPhoto(encoded: String) {
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let(binding.ivProfileImage::setImageBitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
