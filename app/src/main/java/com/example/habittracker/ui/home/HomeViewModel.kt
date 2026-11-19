package com.example.habittracker.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.habittracker.HabitTrackerApp
import com.example.habittracker.model.CreateHabitRequest
import com.example.habittracker.model.CustomScheduleRequest
import com.example.habittracker.model.HabitResponse
import com.example.habittracker.model.ScheduleResponse
import com.example.habittracker.repository.HabitRepository
import com.example.habittracker.repository.ScheduleRepository
import com.example.habittracker.ui.BaseViewModel
import com.example.habittracker.ui.UiState
import java.time.LocalDate

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val container = (application as HabitTrackerApp).container
    private val schedulesRepository: ScheduleRepository = container.scheduleRepository
    private val habitsRepository: HabitRepository = container.habitRepository

    private val _schedules = MutableLiveData<UiState<List<ScheduleResponse>>>(UiState.Idle)
    val schedules: LiveData<UiState<List<ScheduleResponse>>> = _schedules
    private val _sampleDayState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val sampleDayState: LiveData<UiState<Unit>> = _sampleDayState

    fun loadSchedules(day: String) {
        execute(_schedules) { schedulesRepository.getScheduleByDay(day) }
    }

    fun addSampleDay() {
        execute(_sampleDayState) {
            var habits: List<HabitResponse> = habitsRepository.getMyHabits()
            val createStarterHabits = habits.isEmpty()
            if (createStarterHabits) {
                val categories = habitsRepository.getCategories()
                check(categories.isNotEmpty()) { "No habit categories are available yet." }
                val samples = listOf(
                    Triple("Drink water", "Stay refreshed throughout the day.", "8 glasses a day") to listOf("water", "hydration"),
                    Triple("Read 10 pages", "Make a little time for reading.", "10 pages") to listOf("read", "book"),
                    Triple("Morning walk", "Get outside and start moving.", "20 minutes") to listOf("exercise", "walk", "run"),
                    Triple("Evening journal", "Reflect on the day before bed.", "Write one entry") to listOf("write", "journal")
                )
                habits = samples.map { (sample, keywords) ->
                    val category = categories.find { candidate ->
                        keywords.any { candidate.name.contains(it, ignoreCase = true) }
                    } ?: categories.first()
                    habitsRepository.create(
                        CreateHabitRequest(
                            name = sample.first,
                            description = sample.second,
                            categoryId = category.id.toInt(),
                            goal = sample.third
                        )
                    )
                }
            }

            val today = LocalDate.now().toString()
            val times = listOf("08:00", "10:00", "17:30", "20:30")
            habits.take(4).forEachIndexed { index, habit ->
                schedulesRepository.createCustom(
                    CustomScheduleRequest(
                        habitId = habit.id.toInt(),
                        date = "${today}T00:00:00",
                        start_time = "${today}T${times[index]}:00",
                        duration_minutes = 20,
                        notes = "Sample activity"
                    )
                )
            }
            loadSchedules(today)
            Unit
        }
    }

    fun consumeSampleDayState() = clear(_sampleDayState)
    fun consumeSchedulesState() = clear(_schedules)
}
