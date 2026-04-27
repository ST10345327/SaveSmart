/**
 * Reference:
 * - Android Developers (2024) Fragment overview. Google LLC.
 *   Available at: https://developer.android.com/guide/fragments (Accessed: 24 March 2026).
 * - Android Developers (2024) View Binding. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/view-binding (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.savesmart.R
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentRewardsBinding
import com.example.savesmart.ui.ViewModelFactory
import com.example.savesmart.util.SessionManager

/**
 * RewardsFragment — Displays user points, levels, and unlocked badges (R19, R20, R21).
 *
 * GitHub commit suggestion:
 *   [rewards] implement RewardsFragment with level and badge visualization (R19, R20, R21)
 *   - Integrated points tracking and level progression
 *   - Added navigation to Leaderboard
 *   Refs: R19, R20, R21, T06
 */
class RewardsFragment : Fragment() {

    private val TAG = "RewardsFragment"
    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RewardsViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var badgeAdapter: BadgeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: entry")
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RewardsViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadUserData(userId)
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Initializing badge grid")
        badgeAdapter = BadgeAdapter()
        binding.rvBadges.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = badgeAdapter
        }
    }

    private fun setupListeners() {
        binding.btnViewLeaderboard.setOnClickListener {
            Log.d(TAG, "setupListeners: Navigating to Leaderboard")
            findNavController().navigate(R.id.action_rewardsFragment_to_leaderboardFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.points.observe(viewLifecycleOwner) { points ->
            Log.d(TAG, "observeViewModel: Updated points: $points")
            binding.tvTotalPoints.text = "$points pts"
            
            // Requirement R21: 1000 pts per level
            val currentLevel = (points / 1000) + 1
            val pointsInCurrentLevel = points % 1000
            val progress = (pointsInCurrentLevel.toFloat() / 1000f * 100).toInt()
            
            binding.tvLevel.text = "Level $currentLevel"
            binding.progressLevel.progress = progress
            binding.tvProgressDesc.text = "$pointsInCurrentLevel/1000 to Level ${currentLevel + 1}"
        }
        
        val userId = sessionManager.getUserId()
        viewModel.getEarnedBadges(userId).observe(viewLifecycleOwner) { badges ->
            Log.d(TAG, "observeViewModel: received ${badges.size} earned badges")
            badgeAdapter.submitList(badges)
            
            if (badges.isEmpty()) {
                binding.tvUnlockedBadges.text = getString(R.string.label_unlocked_badges) + " (None yet)"
            } else {
                binding.tvUnlockedBadges.text = getString(R.string.label_unlocked_badges) + " (${badges.size})"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: cleanup")
        _binding = null
    }
}
