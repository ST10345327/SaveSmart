package com.example.savesmart.ui.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: SaveSmartRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val TAG = "OnboardingViewModel"

    companion object {
        private const val KEY_MIN_GOAL = "min_goal"
        private const val KEY_MAX_GOAL = "max_goal"
        private const val KEY_CAT_NAME = "cat_name"
    }

    private val _onboardingStep = MutableLiveData<Int>()
    val onboardingStep: LiveData<Int> = _onboardingStep

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    var minGoal: Long
        get() = savedStateHandle[KEY_MIN_GOAL] ?: 0L
        set(value) { savedStateHandle[KEY_MIN_GOAL] = value }

    var maxGoal: Long
        get() = savedStateHandle[KEY_MAX_GOAL] ?: 0L
        set(value) { savedStateHandle[KEY_MAX_GOAL] = value }

    var firstCategoryName: String
        get() = savedStateHandle[KEY_CAT_NAME] ?: ""
        set(value) { savedStateHandle[KEY_CAT_NAME] = value }

    /**
     * Requirement R23: Load the last saved onboarding step for the user.
     */
    fun loadOnboardingStep(userId: Int) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _onboardingStep.value = user?.onboardingStep ?: 0
        }
    }

    /**
     * Requirement R23: Persist current step to database for resumption.
     * Also saves partial data if applicable.
     */
    fun saveStepProgress(userId: Int, step: Int) {
        viewModelScope.launch {
            if (userId <= 0) return@launch
            
            try {
                repository.updateOnboardingStep(userId, step)
                
                // If they just finished step 0, save budget goals
                if (step == 1) {
                    val user = repository.getUserById(userId)
                    user?.let {
                        repository.updateUser(it.copy(
                            minMonthlyBudget = minGoal,
                            maxMonthlyBudget = maxGoal
                        ))
                    }
                }
                
                // If they just finished step 1, save category
                if (step == 2 && firstCategoryName.isNotEmpty()) {
                    val category = Category(
                        userId = userId,
                        name = firstCategoryName,
                        colorHex = "#1A6FE8",
                        maxGoalMilliunits = 0,
                        minGoalMilliunits = 0
                    )
                    repository.insertCategory(category)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving partial onboarding progress", e)
            }
        }
    }

    fun completeOnboarding(userId: Int) {
        viewModelScope.launch {
            try {
                if (userId <= 0) {
                    _operationSuccess.postValue(false)
                    return@launch
                }
                
                // 1. Finalize user data (mark complete)
                val user = repository.getUserById(userId)
                user?.let {
                    val updatedUser = it.copy(
                        minMonthlyBudget = minGoal.takeIf { g -> g > 0 } ?: it.minMonthlyBudget,
                        maxMonthlyBudget = maxGoal.takeIf { g -> g > 0 } ?: it.maxMonthlyBudget,
                        onboardingComplete = true,
                        onboardingStep = 2 // Ensure step is marked as final
                    )
                    repository.updateUser(updatedUser)
                }

                // 2. Create category if not already created in saveStepProgress
                // Check if user already has categories to avoid duplicates on resume-then-finish
                val existingCategories = repository.getCategoriesWithSpending(userId, 0, System.currentTimeMillis())
                if (firstCategoryName.isNotEmpty() && existingCategories.none { it.name == firstCategoryName }) {
                    val category = Category(
                        userId = userId,
                        name = firstCategoryName,
                        colorHex = "#1A6FE8",
                        maxGoalMilliunits = 0,
                        minGoalMilliunits = 0
                    )
                    repository.insertCategory(category)
                }

                // 3. Award onboarding points (R19)
                repository.awardPoints(userId, 50)

                _operationSuccess.postValue(true)
            } catch (e: Exception) {
                Log.e(TAG, "Error completing onboarding", e)
                _operationSuccess.postValue(false)
            }
        }
    }
}
