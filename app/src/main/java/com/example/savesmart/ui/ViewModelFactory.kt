package com.example.savesmart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savesmart.data.repository.SaveSmartRepository
import com.example.savesmart.ui.auth.AuthViewModel
import com.example.savesmart.ui.categories.CategoriesViewModel
import com.example.savesmart.ui.dashboard.DashboardViewModel
import com.example.savesmart.ui.expense.ExpenseViewModel
import com.example.savesmart.ui.onboarding.OnboardingViewModel
import com.example.savesmart.ui.reports.ReportsViewModel
import com.example.savesmart.ui.rewards.RewardsViewModel

class ViewModelFactory(private val repository: SaveSmartRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> 
                AuthViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> 
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> 
                ExpenseViewModel(repository) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> 
                OnboardingViewModel(repository) as T
            modelClass.isAssignableFrom(RewardsViewModel::class.java) -> 
                RewardsViewModel(repository) as T
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> 
                CategoriesViewModel(repository) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> 
                ReportsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
