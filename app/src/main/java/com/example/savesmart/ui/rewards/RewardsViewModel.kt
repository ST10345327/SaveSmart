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
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.UserBadge
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

/**
 * RewardsViewModel — Manages points, levels, and badges (R19, R20, R21).
 */
class RewardsViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "RewardsViewModel"

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int> = _level

    /**
     * Requirement R20: Observe earned badges.
     */
    fun getEarnedBadges(userId: Int): LiveData<List<Badge>> {
        Log.d(TAG, "getEarnedBadges: userId $userId")
        // Note: BadgeDao has getEarnedBadgesLive
        return repository.getEarnedBadgesLive(userId)
    }

    /**
     * Requirement R19: Points are updated in the User entity.
     */
    fun loadUserData(userId: Int) {
        Log.d(TAG, "loadUserData: userId $userId")
        viewModelScope.launch {
            repository.getUserLive(userId).observeForever { user ->
                user?.let {
                    _points.postValue(it.totalPoints)
                    _level.postValue(it.level)
                }
            }
        }
    }
}
