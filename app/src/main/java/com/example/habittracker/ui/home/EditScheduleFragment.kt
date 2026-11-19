package com.example.habittracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.databinding.FragmentEditScheduleBinding
import com.example.habittracker.model.UpdateScheduleRequest
import com.example.habittracker.ui.UiState

class EditScheduleFragment : Fragment() {
    private var _binding: FragmentEditScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditScheduleViewModel by viewModels()
    private var scheduleId: Long = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditScheduleBinding.inflate(inflater, container, false)
        scheduleId = arguments?.getLong("scheduleId", -1) ?: -1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.spStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Planned", "Completed", "Skipped"))
        viewModel.schedule.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                val schedule = state.data
                binding.etStartTime.setText(schedule.startTime?.toString().orEmpty())
                binding.etEndTime.setText(schedule.endTime?.toString().orEmpty())
                binding.etDuration.setText(schedule.durationMinutes?.toString().orEmpty())
                binding.etNotes.setText(schedule.notes.orEmpty())
                val statuses = listOf("Planned", "Completed", "Skipped")
                statuses.indexOf(schedule.status).takeIf { it >= 0 }?.let(binding.spStatus::setSelection)
                binding.etParticipantIds.setText(
                    schedule.participants.joinToString(",") { it.id.toString() }
                )
            } else if (state is UiState.Error) {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            binding.btnSaveSchedule.isEnabled = state !is UiState.Loading
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeSaveState()
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Schedule updated.", Toast.LENGTH_SHORT).show()
                    viewModel.consumeSaveState()
                    findNavController().navigateUp()
                }
            }
        }
        viewModel.load(scheduleId)
        binding.btnSaveSchedule.setOnClickListener { saveSchedule() }
    }

    private fun saveSchedule() {
        val participantIds = binding.etParticipantIds.text.toString()
            .split(',').mapNotNull { it.trim().toIntOrNull() }
            .takeIf { it.isNotEmpty() }
        val duration = binding.etDuration.text.toString().toIntOrNull()
        val request = UpdateScheduleRequest(
            start_time = binding.etStartTime.text.toString().trim().ifBlank { null },
            end_time = binding.etEndTime.text.toString().trim().ifBlank { null },
            duration_minutes = duration,
            status = binding.spStatus.selectedItem?.toString(),
            participantIds = participantIds,
            notes = binding.etNotes.text.toString().trim()
        )
        viewModel.save(scheduleId, request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
