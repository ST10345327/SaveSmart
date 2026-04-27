package com.example.savesmart.ui.rewards

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.R
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.databinding.ItemBadgeBinding

/**
 * Adapter for displaying earned badges in a grid (Requirement R20).
 */
class BadgeAdapter : ListAdapter<Badge, BadgeAdapter.BadgeViewHolder>(BadgeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BadgeViewHolder(private val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        
        @SuppressLint("DiscouragedApi")
        fun bind(badge: Badge) {
            binding.tvBadgeName.text = badge.name
            binding.tvBadgeDescription.text = badge.description
            
            // Map icon string name to actual resource ID
            val resourceId = when(badge.iconResName) {
                "ic_badge_first_save" -> R.drawable.ic_badge_first_save
                "ic_badge_quick_logger" -> R.drawable.ic_badge_quick_logger
                "ic_badge_streak_7" -> R.drawable.ic_badge_streak_7
                "ic_badge_streak_30" -> R.drawable.ic_badge_streak_30
                "ic_badge_budget_master" -> R.drawable.ic_badge_budget_master
                "ic_badge_zero_spend" -> R.drawable.ic_badge_zero_spend
                "ic_badge_goal_crusher" -> R.drawable.ic_badge_goal_crusher
                else -> 0
            }
            
            if (resourceId != 0) {
                binding.ivBadgeIcon.setImageResource(resourceId)
                binding.ivBadgeIcon.imageTintList = null 
            } else {
                binding.ivBadgeIcon.setImageResource(android.R.drawable.btn_star_big_on)
            }
        }
    }

    class BadgeDiffCallback : DiffUtil.ItemCallback<Badge>() {
        override fun areItemsTheSame(oldItem: Badge, newItem: Badge): Boolean =
            oldItem.badgeId == newItem.badgeId

        override fun areContentsTheSame(oldItem: Badge, newItem: Badge): Boolean =
            oldItem == newItem
    }
}
