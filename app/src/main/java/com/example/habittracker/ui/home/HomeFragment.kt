package com.example.habittracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentHomeBinding
import com.example.habittracker.ui.UiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HomeScheduleAdapter { schedule ->
            val bundle = Bundle().apply { putLong("scheduleId", schedule.id) }
            findNavController().navigate(
                R.id.action_homeFragment_to_scheduleDetailsFragment,
                bundle
            )
        }
        binding.rvSchedules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedules.adapter = adapter
        binding.rvSchedules.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
        binding.tvTodayDate.text = LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d")
        )
        binding.fabAddHabit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addHabitFragment)
        }
        binding.fabCreateSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createScheduleFragment)
        }
        binding.btnAddSampleDay.setOnClickListener { viewModel.addSampleDay() }
        observeState()
        viewModel.loadSchedules(LocalDate.now().toString())
    }

    private fun observeState() {
        viewModel.schedules.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeSchedulesState()
                }
                is UiState.Success -> {
                    val items = state.data.sortedBy { it.startTime }
                    adapter.submitSchedules(items)
                    binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    binding.rvSchedules.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                    binding.tvScheduleCount.text = if (items.isEmpty()) {
                        "YOUR SCHEDULE"
                    } else {
                        "YOUR SCHEDULE · ${items.size} ${if (items.size == 1) "ACTIVITY" else "ACTIVITIES"}"
                    }
                }
            }
        }
        viewModel.sampleDayState.observe(viewLifecycleOwner) { state ->
            binding.btnAddSampleDay.isEnabled = state !is UiState.Loading
            binding.progressBar.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            when (state) {
                UiState.Idle, UiState.Loading -> Unit
                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeSampleDayState()
                }
                is UiState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Sample habits and today's schedule added.",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.consumeSampleDayState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
