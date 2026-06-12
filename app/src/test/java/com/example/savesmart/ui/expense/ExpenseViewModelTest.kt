package com.example.savesmart.ui.expense

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SaveSmartRepository
    private lateinit var viewModel: ExpenseViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        
        repository = mockk()
        viewModel = ExpenseViewModel(repository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCategories updates categories live data`() = runTest {
        val userId = 1
        val categories = listOf(
            Category(
                categoryId = 1,
                userId = userId,
                name = "Food",
                minGoalMilliunits = 0,
                maxGoalMilliunits = 0
            )
        )
        val categories = listOf(Category(categoryId = 1, userId = 1, name = "Food", minGoalMilliunits = 0, maxGoalMilliunits = 0))
=======
        every { repository.getCategoriesForUserLive(userId) } returns liveData
        val categories = listOf(
            Category(
                categoryId = 1,
                userId = userId,
        viewModel.loadCategories(userId)
                minGoalMilliunits = 0,
                maxGoalMilliunits = 0
            )
        )
>>>>>>> ce038ab2d766f20e06e2c9ab1a9ab065e6b9ee31
        val liveData = MutableLiveData<List<Category>>(categories)
        
        every { repository.getCategoriesForUserLive(userId) } returns liveData

<<<<<<< HEAD
        // switchMap requires observation to trigger
        viewModel.categories.observeForever {}

        viewModel.loadCategories(1)
=======
        viewModel.loadCategories(userId)
>>>>>>> ce038ab2d766f20e06e2c9ab1a9ab065e6b9ee31
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(categories, viewModel.categories.value)
    }
}