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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savesmart.data.database.SaveSmartDatabase
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.databinding.FragmentLeaderboardBinding
import com.example.savesmart.ui.ViewModelFactory

/**
 * LeaderboardFragment — Displays a ranked list of all users by points (Requirement R22).
 *
 * GitHub commit suggestion:
 *   [rewards] implement LeaderboardFragment with global rankings (R22)
 *   - Integrated with RewardsViewModel for global ranked data
 *   - Added RecyclerView with rank-based styling
 *   Refs: R22, T06, CS1
 */
class LeaderboardFragment : Fragment() {

    private val TAG = "LeaderboardFragment"
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RewardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: started")
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
        Log.d(TAG, "setupRecyclerView: initializing adapter")
        val adapter = LeaderboardAdapter()
        binding.rvLeaderboard.adapter = adapter
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.getAllRankedUsers().observe(viewLifecycleOwner) { users ->
            Log.d(TAG, "observeViewModel: received ${users.size} ranked users")
            (binding.rvLeaderboard.adapter as? LeaderboardAdapter)?.submitList(users)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: clearing binding")
        _binding = null
    }
}
