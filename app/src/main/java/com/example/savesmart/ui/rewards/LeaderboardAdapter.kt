package com.example.savesmart.ui.rewards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.savesmart.data.entity.User
import com.example.savesmart.databinding.ItemLeaderboardBinding

class LeaderboardAdapter : ListAdapter<User, LeaderboardAdapter.LeaderboardViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, rank: Int) {
            binding.tvRank.text = rank.toString()
            binding.tvUsername.text = user.username
            binding.tvUserLevel.text = "Level ${user.level}"
            binding.tvPoints.text = "${user.totalPoints} pts"
            
            // Highlight top 3
            when (rank) {
                1 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#FFD700")) // Gold
                2 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#C0C0C0")) // Silver
                3 -> binding.tvRank.setTextColor(android.graphics.Color.parseColor("#CD7F32")) // Bronze
                else -> binding.tvRank.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.userId == newItem.userId
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}
