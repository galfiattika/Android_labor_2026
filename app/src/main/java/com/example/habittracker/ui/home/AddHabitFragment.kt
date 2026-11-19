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
import com.example.habittracker.databinding.FragmentAddHabitBinding
import com.example.habittracker.model.CreateHabitRequest
import com.example.habittracker.ui.UiState

class AddHabitFragment : Fragment() {
    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddHabitViewModel by viewModels()
    private var categoryIds: List<Int> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSaveHabit.setOnClickListener { saveHabit() }
        binding.btnCancelHabit.setOnClickListener { findNavController().navigateUp() }
        viewModel.categories.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                is UiState.Success -> {
                    categoryIds = state.data.map { it.id.toInt() }
                    binding.spHabitCategory.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        state.data.map { it.name }
                    )
                }
            }
        }
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            binding.btnSaveHabit.isEnabled = state !is UiState.Loading
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeSaveState()
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Habit created.", Toast.LENGTH_SHORT).show()
                    viewModel.consumeSaveState()
                    findNavController().navigateUp()
                }
            }
        }
        viewModel.loadCategories()
    }

    private fun saveHabit() {
        val name = binding.etHabitName.text.toString().trim()
        val description = binding.etHabitDescription.text.toString().trim()
        val goal = binding.etHabitGoal.text.toString().trim()
        val categoryId = categoryIds.getOrNull(binding.spHabitCategory.selectedItemPosition)
        if (name.isBlank() || goal.isBlank() || categoryId == null) {
            Toast.makeText(requireContext(), "Name, category and goal are required.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.createHabit(
            CreateHabitRequest(
                name = name,
                description = description.ifBlank { null },
                categoryId = categoryId,
                goal = goal
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
