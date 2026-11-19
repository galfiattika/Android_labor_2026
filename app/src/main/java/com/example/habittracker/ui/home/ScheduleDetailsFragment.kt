package com.example.habittracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentScheduleDetailsBinding
import com.example.habittracker.model.CreateProgressRequest
import com.example.habittracker.ui.UiState
import java.time.LocalDate

class ScheduleDetailsFragment : Fragment() {
    private var _binding: FragmentScheduleDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleDetailsViewModel by viewModels()
    private var scheduleId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleDetailsBinding.inflate(inflater, container, false)
        scheduleId = arguments?.getLong("scheduleId", -1) ?: -1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAddProgress.setOnClickListener { addProgress() }
        binding.btnEditNotes.setOnClickListener { editNotes() }
        binding.btnEditSchedule.setOnClickListener {
            val bundle = Bundle().apply { putLong("scheduleId", scheduleId) }
            findNavController().navigate(
                R.id.action_scheduleDetailsFragment_to_editScheduleFragment,
                bundle
            )
        }
        binding.btnDeleteSchedule.setOnClickListener { confirmDelete() }
        viewModel.schedule.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                is UiState.Success -> renderSchedule(state.data)
            }
        }
        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeActionState()
                }
                is UiState.Success -> {
                    when (state.data) {
                        ScheduleAction.DELETED -> {
                            Toast.makeText(requireContext(), "Schedule deleted.", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        ScheduleAction.NOTES_UPDATED -> {
                            Toast.makeText(requireContext(), "Notes updated.", Toast.LENGTH_SHORT).show()
                            viewModel.load(scheduleId)
                        }
                        ScheduleAction.PROGRESS_ADDED -> {
                            Toast.makeText(requireContext(), "Progress added.", Toast.LENGTH_SHORT).show()
                            binding.etProgressNotes.text?.clear()
                            binding.etLoggedTime.text?.clear()
                            binding.cbCompleted.isChecked = false
                            viewModel.load(scheduleId)
                        }
                    }
                    viewModel.consumeActionState()
                }
            }
        }
        viewModel.load(scheduleId)
    }

    private fun renderSchedule(schedule: com.example.habittracker.model.ScheduleResponse) {
        binding.tvHabitName.text = schedule.habit?.name ?: "Habit"
        binding.tvScheduleTime.text = "Status: ${schedule.status}"
        binding.tvDuration.text = schedule.durationMinutes?.let { "Duration: $it min" }.orEmpty()
        binding.tvNotes.text = schedule.notes?.takeIf { it.isNotBlank() }?.let { "Notes: $it" } ?: "No notes"
        val completed = schedule.progress.count { it.is_completed == true }
        binding.progressCompletion.progress =
            if (schedule.progress.isEmpty()) 0 else completed * 100 / schedule.progress.size
        binding.tvActivities.text = if (schedule.progress.isEmpty()) {
            "No activity logged yet."
        } else {
            schedule.progress.joinToString("\n") { entry ->
                "${entry.date ?: ""}: " +
                    (if (entry.is_completed == true) "Completed" else "Logged") +
                    (entry.logged_time?.let { " — $it min" } ?: "") +
                    (entry.notes?.let { " — $it" } ?: "")
            }
        }
    }

    private fun editNotes() {
        val input = EditText(requireContext()).apply {
            setText(
                binding.tvNotes.text.toString()
                    .removePrefix("Notes: ")
                    .takeUnless { it == "No notes" }
                    .orEmpty()
            )
            minLines = 3
            setPadding(32, 24, 32, 24)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Edit notes")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                viewModel.updateNotes(scheduleId, input.text.toString().trim())
            }
            .show()
    }

    private fun addProgress() {
        val logged = binding.etLoggedTime.text.toString().toIntOrNull()
        if (binding.etLoggedTime.text?.isNotBlank() == true && (logged == null || logged < 0)) {
            Toast.makeText(requireContext(), "Enter a valid logged time.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.addProgress(
            CreateProgressRequest(
                scheduleId = scheduleId,
                date = LocalDate.now().toString(),
                logged_time = logged,
                notes = binding.etProgressNotes.text.toString().trim().ifBlank { null },
                is_completed = binding.cbCompleted.isChecked
            )
        )
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete schedule")
            .setMessage("Are you sure?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(scheduleId) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
