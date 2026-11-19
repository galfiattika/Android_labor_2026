package com.example.habittracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.databinding.ItemHomeScheduleBinding
import com.example.habittracker.databinding.ItemScheduleSectionBinding
import com.example.habittracker.model.ScheduleResponse
import java.time.format.DateTimeFormatter

sealed class ScheduleListItem {
    data class Section(val title: String) : ScheduleListItem()
    data class Schedule(val value: ScheduleResponse) : ScheduleListItem()
}

class HomeScheduleAdapter(private val onItemClick: (ScheduleResponse) -> Unit = {}) :
    ListAdapter<ScheduleListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    fun submitSchedules(schedules: List<ScheduleResponse>) {
        val sections = listOf(
            "Morning" to schedules.filter { (it.startTime?.hour ?: 0) < 12 },
            "Afternoon" to schedules.filter {
                val hour = it.startTime?.hour ?: 0
                hour in 12..17
            },
            "Night" to schedules.filter { (it.startTime?.hour ?: 0) >= 18 }
        )
        submitList(sections.flatMap { (title, items) ->
            if (items.isEmpty()) emptyList()
            else listOf(ScheduleListItem.Section(title)) + items.map(ScheduleListItem::Schedule)
        })
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position) is ScheduleListItem.Section) VIEW_SECTION else VIEW_SCHEDULE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_SECTION) {
            SectionViewHolder(ItemScheduleSectionBinding.inflate(inflater, parent, false))
        } else {
            ScheduleViewHolder(
                ItemHomeScheduleBinding.inflate(inflater, parent, false),
                onItemClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ScheduleListItem.Section -> (holder as SectionViewHolder).bind(item)
            is ScheduleListItem.Schedule -> (holder as ScheduleViewHolder).bind(item.value)
        }
    }

    private class SectionViewHolder(
        private val binding: ItemScheduleSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleListItem.Section) {
            val icon = when (item.title) {
                "Morning" -> "☀"
                "Afternoon" -> "🌤"
                else -> "☾"
            }
            binding.tvSectionTitle.text = "$icon  ${item.title}"
        }
    }

    private class ScheduleViewHolder(
        private val binding: ItemHomeScheduleBinding,
        private val onItemClick: (ScheduleResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleResponse) {
            binding.root.setOnClickListener { onItemClick(item) }
            binding.tvTitle.text = item.habit?.name ?: "Unknown Habit"
            binding.tvTime.text = formatTime(item)
            val isCompleted = item.status.equals("Completed", ignoreCase = true) ||
                item.progress.any { it.is_completed == true }
            binding.ivStatus.setImageResource(
                if (isCompleted) android.R.drawable.checkbox_on_background
                else android.R.drawable.checkbox_off_background
            )
            binding.ivStatus.imageTintList = ContextCompat.getColorStateList(
                binding.root.context,
                if (isCompleted) R.color.success else R.color.text_secondary
            )

            val notes = item.notes
            if (notes.isNullOrBlank()) {
                binding.tvNotes.visibility = View.GONE
            } else {
                binding.tvNotes.text = notes
                binding.tvNotes.visibility = View.VISIBLE
            }
            binding.ivIcon.setImageResource(resolveCategoryIcon(item.habit?.category?.name.orEmpty()))
        }

        private fun formatTime(item: ScheduleResponse): String {
            val start = item.startTime ?: return "No schedule time"
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return item.endTime?.let { "${start.format(formatter)} - ${it.format(formatter)}" }
                ?: start.format(formatter)
        }

        private fun resolveCategoryIcon(categoryName: String): Int =
            when (categoryName.lowercase()) {
                "exercise", "running", "fitness" -> android.R.drawable.ic_menu_directions
                "reading", "study" -> android.R.drawable.ic_menu_edit
                "hydration", "water" -> android.R.drawable.ic_lock_lock
                "writing", "journal" -> android.R.drawable.ic_menu_sort_by_size
                else -> android.R.drawable.ic_menu_agenda
            }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ScheduleListItem>() {
        override fun areItemsTheSame(oldItem: ScheduleListItem, newItem: ScheduleListItem): Boolean =
            when {
                oldItem is ScheduleListItem.Section && newItem is ScheduleListItem.Section ->
                    oldItem.title == newItem.title
                oldItem is ScheduleListItem.Schedule && newItem is ScheduleListItem.Schedule ->
                    oldItem.value.id == newItem.value.id
                else -> false
            }

        override fun areContentsTheSame(oldItem: ScheduleListItem, newItem: ScheduleListItem): Boolean =
            oldItem == newItem
    }

    private companion object {
        const val VIEW_SECTION = 0
        const val VIEW_SCHEDULE = 1
    }
}
