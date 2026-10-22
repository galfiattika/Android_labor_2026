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
import com.example.habittracker.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (email.isBlank() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.auth_fields_required,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.login(email, password)
            }
        }
        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        viewModel.authState.observe(viewLifecycleOwner, ::renderAuthState)
    }

    private fun renderAuthState(state: AuthUiState) {
        val isLoading = state is AuthUiState.Loading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoToRegister.isEnabled = !isLoading

        when (state) {
            AuthUiState.Idle, AuthUiState.Loading -> Unit
            is AuthUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                viewModel.consumeState()
            }
            is AuthUiState.Success -> {
                if (state.isLogin) {
                    Toast.makeText(
                        requireContext(),
                        R.string.login_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.consumeState()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
