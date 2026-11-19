package com.example.habittracker.ui.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentProfileBinding
import com.example.habittracker.ui.UiState

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.btnAddHabit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addHabitFragment)
        }
        binding.btnLogout.setOnClickListener { confirmLogout() }
        viewModel.profile.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                is UiState.Success -> renderProfile(state.data)
            }
        }
        viewModel.logoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeLogoutState()
                }
                is UiState.Success -> {
                    viewModel.consumeLogoutState()
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        viewModel.load()
    }

    private fun renderProfile(data: ProfileScreenData) {
        binding.tvUsername.text = data.profile.username ?: "User"
        binding.tvEmail.text = data.profile.email.orEmpty()
        data.profile.profileImageBase64?.let { encoded ->
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let(binding.ivProfileImage::setImageBitmap)
        }
        binding.habitListContainer.removeAllViews()
        binding.tvHabits.visibility = if (data.habits.isEmpty()) View.VISIBLE else View.GONE
        data.habits.forEach { habit ->
            val related = data.schedules.filter { it.habit?.id == habit.id }
            val completed = related.count { schedule ->
                schedule.status.equals("Completed", ignoreCase = true) ||
                    schedule.progress.any { it.is_completed == true }
            }
            val percent = if (related.isEmpty()) 0 else completed * 100 / related.size
            addHabitProgressRow(habit.name, percent)
        }
    }

    private fun addHabitProgressRow(name: String, percent: Int) {
        val context = requireContext()
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, context.resources.getDimensionPixelSize(R.dimen.habit_row_padding), 0, 0)
        }
        val heading = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        heading.addView(TextView(context).apply {
            text = name
            textSize = 15f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        heading.addView(TextView(context).apply {
            text = "$percent%"
            textSize = 13f
            setTextColor(ContextCompat.getColor(context, R.color.teal_200))
        })
        row.addView(heading)
        row.addView(ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = percent
            progressTintList = ContextCompat.getColorStateList(context, R.color.teal_200)
            progressBackgroundTintList = ContextCompat.getColorStateList(context, R.color.surface_dark)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.habit_progress_height)
            ).apply {
                topMargin = context.resources.getDimensionPixelSize(R.dimen.habit_progress_top_margin)
            }
        })
        binding.habitListContainer.addView(row)
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log out")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Log out") { _, _ -> logout() }
            .show()
    }

    private fun logout() {
        viewModel.logout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
