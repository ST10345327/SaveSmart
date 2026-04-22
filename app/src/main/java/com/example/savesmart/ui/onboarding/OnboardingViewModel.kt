package com.example.savesmart.ui.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: SaveSmartRepository) : ViewModel() {

    private val TAG = "OnboardingViewModel"

    private val _onboardingStep = MutableLiveData<Int>(0)
    val onboardingStep: LiveData<Int> = _onboardingStep

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    var minGoal: Long = 0
    var maxGoal: Long = 0
    var firstCategoryName: String = ""

    fun setStep(step: Int) {
        _onboardingStep.value = step
    }

    fun completeOnboarding(userId: Int) {
        viewModelScope.launch {
            try {
                // 1. Update user budget goals
                val user = repository.getUserById(userId)
                user?.let {
                    val updatedUser = it.copy(
                        minMonthlyBudget = minGoal,
                        maxMonthlyBudget = maxGoal,
                        onboardingComplete = true
                    )
                    repository.updateUser(updatedUser)
                }

                // 2. Create first category if provided
                if (firstCategoryName.isNotEmpty()) {
                    val category = Category(
                        userId = userId,
                        name = firstCategoryName,
                        colorHex = "#1A6FE8", // Default brand color
                        maxGoalMilliunits = 0, // Individual category goals can be set later
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
