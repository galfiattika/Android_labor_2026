package com.example.habittracker.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentCreateScheduleBinding
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.model.CustomScheduleRequest
import com.example.habittracker.model.RecurringScheduleRequest
import com.example.habittracker.ui.UiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateScheduleFragment : Fragment() {
    private var _binding: FragmentCreateScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateScheduleViewModel by viewModels()
    private var habits: List<HabitResponse> = emptyList()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etScheduleDate.setText(LocalDate.now().format(dateFormatter))
        binding.etScheduleStart.setText("08:00")
        binding.etScheduleEnd.setText("09:00")
        binding.spRepeat.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Once", "Every day", "Weekdays", "Weekends")
        )
        binding.etScheduleDate.setOnClickListener { chooseDate() }
        binding.etScheduleStart.setOnClickListener {
            chooseTime(binding.etScheduleStart.text.toString()) { binding.etScheduleStart.setText(it) }
        }
        binding.etScheduleEnd.setOnClickListener {
            chooseTime(binding.etScheduleEnd.text.toString()) { binding.etScheduleEnd.setText(it) }
        }
        binding.btnSaveSchedule.setOnClickListener { saveSchedule() }
        binding.btnCancelSchedule.setOnClickListener { findNavController().navigateUp() }
        viewModel.habits.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                is UiState.Success -> {
                    habits = state.data
                    if (habits.isEmpty()) {
                        Toast.makeText(requireContext(), "Add a habit before creating its schedule.", Toast.LENGTH_LONG).show()
                    } else {
                        binding.spScheduleHabit.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            habits.map { it.name }
                        )
                    }
                }
            }
        }
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            binding.btnSaveSchedule.isEnabled = state !is UiState.Loading
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeSaveState()
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Schedule created.", Toast.LENGTH_SHORT).show()
                    viewModel.consumeSaveState()
                    findNavController().navigateUp()
                }
            }
        }
        viewModel.loadHabits()
    }

    private fun chooseDate() {
        val current = runCatching { LocalDate.parse(binding.etScheduleDate.text.toString()) }
            .getOrElse { LocalDate.now() }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                binding.etScheduleDate.setText(
                    LocalDate.of(year, month + 1, day).format(dateFormatter)
                )
            },
            current.year,
            current.monthValue - 1,
            current.dayOfMonth
        ).show()
    }

    private fun chooseTime(initial: String, onSelected: (String) -> Unit) {
        val current = runCatching { LocalTime.parse(initial, timeFormatter) }
            .getOrElse { LocalTime.of(8, 0) }
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                onSelected(LocalTime.of(hour, minute).format(timeFormatter))
            },
            current.hour,
            current.minute,
            false
        ).show()
    }

    private fun saveSchedule() {
        val habit = habits.getOrNull(binding.spScheduleHabit.selectedItemPosition)
        val date = runCatching { LocalDate.parse(binding.etScheduleDate.text.toString()) }.getOrNull()
        val start = runCatching { LocalTime.parse(binding.etScheduleStart.text.toString(), timeFormatter) }.getOrNull()
        val endText = binding.etScheduleEnd.text.toString().trim()
        val end = if (endText.isBlank()) null
        else runCatching { LocalTime.parse(endText, timeFormatter) }.getOrNull()
        val duration = binding.etDuration.text.toString().toIntOrNull()
        val notes = binding.etScheduleNotes.text.toString().trim().ifBlank { null }
        if (habit == null || date == null || start == null || (endText.isNotBlank() && end == null)) {
            Toast.makeText(requireContext(), "Choose a habit, date and valid times.", Toast.LENGTH_SHORT).show()
            return
        }
        if (end != null && !end.isAfter(start)) {
            Toast.makeText(requireContext(), "End time must be after start time.", Toast.LENGTH_SHORT).show()
            return
        }
        if (duration != null && duration <= 0) {
            Toast.makeText(requireContext(), "Duration must be greater than zero.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateTime = "${date.format(dateFormatter)}T${start.format(timeFormatter)}:00"
        val endTime = end?.let {
            "${date.format(dateFormatter)}T${it.format(timeFormatter)}:00"
        }
        when (binding.spRepeat.selectedItemPosition) {
            1 -> viewModel.createRecurring(
                RecurringScheduleRequest(
                    habitId = habit.id.toInt(),
                    start_time = dateTime,
                    end_time = endTime,
                    duration_minutes = duration,
                    repeatPattern = "daily",
                    notes = notes
                )
            )
            2 -> viewModel.createRecurring(
                RecurringScheduleRequest(
                    habitId = habit.id.toInt(),
                    start_time = dateTime,
                    end_time = endTime,
                    duration_minutes = duration,
                    repeatPattern = "weekdays",
                    notes = notes
                )
            )
            3 -> viewModel.createRecurring(
                RecurringScheduleRequest(
                    habitId = habit.id.toInt(),
                    start_time = dateTime,
                    end_time = endTime,
                    duration_minutes = duration,
                    repeatPattern = "weekends",
                    notes = notes
                )
            )
            else -> viewModel.createCustom(
                CustomScheduleRequest(
                    habitId = habit.id.toInt(),
                    date = "${date.format(dateFormatter)}T00:00:00",
                    start_time = dateTime,
                    end_time = endTime,
                    duration_minutes = duration,
                    notes = notes
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
