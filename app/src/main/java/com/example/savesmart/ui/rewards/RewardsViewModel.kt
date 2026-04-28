/**
 * References:
 * - Android Developers (2024) ViewModel overview. Google LLC.
 *   Available at: https://developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 24 March 2026).
 */

package com.example.savesmart.ui.rewards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.User
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

/**
 * RewardsViewModel — Manages points, levels, and badges (R19, R20, R21, R22).
 */
class RewardsViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "RewardsViewModel"

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int> = _level

    private val _rankedUsers = MutableLiveData<List<User>>()
    val rankedUsers: LiveData<List<User>> = _rankedUsers

    private val _userId = MutableLiveData<Int>()
    val userData: LiveData<com.example.savesmart.data.entity.User?> = _userId.switchMap { userId ->
        repository.getUserLive(userId)
    }

    /**
     * Requirement R20: Observe earned badges.
     */
    fun getEarnedBadges(userId: Int): LiveData<List<Badge>> {
        return repository.getEarnedBadgesLive(userId)
    }

    /**
     * Requirement R19, R21: Load user points and level.
     */
    fun loadUserData(userId: Int) {
        _userId.value = userId
    }

    /**
     * Requirement R22: Load ranked users for leaderboard.
     */
    fun loadLeaderboard() {
        // Data is observed via getAllRankedUsers() which returns LiveData from Room
    }

    fun getAllRankedUsers(): LiveData<List<User>> {
        return repository.getAllUsersRankedLive()
    }
}
