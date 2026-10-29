package com.example.habittracker.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val username = email.substringBefore("@")
            val confirmPassword = binding.etConfirmPassword.text.toString()
            if (email.isBlank() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.registration_fields_required,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != confirmPassword) {
                Toast.makeText(
                    requireContext(),
                    R.string.passwords_do_not_match,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.signUp(email, password, username)
            }
        }
        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.authState.observe(viewLifecycleOwner, ::renderAuthState)
    }

    private fun renderAuthState(state: AuthUiState) {
        val isLoading = state is AuthUiState.Loading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCreateAccount.isEnabled = !isLoading
        binding.btnGoToLogin.isEnabled = !isLoading

        when (state) {
            AuthUiState.Idle, AuthUiState.Loading -> Unit
            is AuthUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                viewModel.consumeState()
            }
            is AuthUiState.Success -> {
                if (!state.isLogin) {
                    Toast.makeText(
                        requireContext(),
                        R.string.registration_success,
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.consumeState()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
