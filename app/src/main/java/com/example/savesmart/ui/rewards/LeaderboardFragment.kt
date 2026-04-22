package com.example.savesmart.ui.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentLeaderboardBinding
import com.example.savesmart.ui.ViewModelFactory

class LeaderboardFragment : Fragment() {

    private val TAG = "LeaderboardFragment"
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RewardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = SaveSmartDatabase.getInstance(requireContext())
        val repository = SaveSmartRepository(db)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RewardsViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = LeaderboardAdapter()
        binding.rvLeaderboard.adapter = adapter
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        // We'll add a ranked users LiveData to the ViewModel to handle R22
        // For now, we can observe all users ordered by points
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
