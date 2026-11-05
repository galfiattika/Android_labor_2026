package com.example.habittracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.databinding.ItemHomeScheduleBinding
import com.example.habittracker.model.ScheduleResponse
import java.time.format.DateTimeFormatter

class HomeScheduleAdapter :
    ListAdapter<ScheduleResponse, HomeScheduleAdapter.ScheduleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemHomeScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduleViewHolder(
        private val binding: ItemHomeScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleResponse) {
            binding.tvTitle.text = item.habit?.name ?: "Unknown Habit"
            binding.tvTime.text = formatTime(item)

            val notes = item.notes
            if (notes.isNullOrBlank()) {
                binding.tvNotes.visibility = View.GONE
            } else {
                binding.tvNotes.text = notes
                binding.tvNotes.visibility = View.VISIBLE
            }

            val categoryName = item.habit?.category?.name.orEmpty()
            binding.ivIcon.setImageResource(resolveCategoryIcon(categoryName))
        }

        private fun formatTime(item: ScheduleResponse): String {
            val start = item.startTime ?: return "No schedule time"
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val end = item.endTime
            return if (end != null) {
                "${start.format(formatter)} - ${end.format(formatter)}"
            } else {
                start.format(formatter)
            }
        }

        private fun resolveCategoryIcon(categoryName: String): Int {
            return when (categoryName.lowercase()) {
                "exercise" -> android.R.drawable.ic_menu_myplaces
                "reading" -> android.R.drawable.ic_menu_edit
                "study" -> android.R.drawable.ic_menu_compass
                "hydration" -> android.R.drawable.ic_lock_lock
                "writing" -> android.R.drawable.ic_menu_sort_by_size
                "running" -> android.R.drawable.ic_menu_directions
                else -> android.R.drawable.ic_menu_agenda
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduleResponse>() {
        override fun areItemsTheSame(oldItem: ScheduleResponse, newItem: ScheduleResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScheduleResponse, newItem: ScheduleResponse): Boolean {
            return oldItem == newItem
        }
    }
}
