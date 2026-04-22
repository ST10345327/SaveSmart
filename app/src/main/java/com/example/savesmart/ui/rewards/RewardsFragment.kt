package com.example.savesmart.ui.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentRewardsBinding
import com.example.savesmart.ui.ViewModelFactory
import com.example.savesmart.util.SessionManager

class RewardsFragment : Fragment() {

    private val TAG = "RewardsFragment"
    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RewardsViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        observeViewModel()

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModel.loadUserData(userId)
        }
    }

    private fun setupRecyclerView() {
        // Badges are shown in a 2-column grid (R20)
        binding.rvBadges.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun observeViewModel() {
        viewModel.points.observe(viewLifecycleOwner) { points ->
            binding.tvTotalPoints.text = "$points pts"
            
            // Calculate progress to next level (R21: 1000 pts per level)
            val currentLevel = (points / 1000) + 1
            val pointsInCurrentLevel = points % 1000
            val progress = (pointsInCurrentLevel.toFloat() / 1000f * 100).toInt()
            
            binding.tvLevel.text = "Level $currentLevel"
            binding.progressLevel.progress = progress
        }
        
        val userId = sessionManager.getUserId()
        viewModel.getEarnedBadges(userId).observe(viewLifecycleOwner) { badges ->
            Log.d(TAG, "observeViewModel: received ${badges.size} earned badges")
            // Logic for showing badges in list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
