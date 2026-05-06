package com.example.savesmart.ui.expense

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.every
import io.mockk.mockk
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
        repository = mockk()
        viewModel = ExpenseViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCategories updates categories live data`() = runTest {
        val categories = listOf(Category(1, 1, "Food", "#FF0000", 0, 0))
        val liveData = MutableLiveData<List<Category>>(categories)
        
        every { repository.getCategoriesForUserLive(1) } returns liveData

        viewModel.loadCategories(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(categories, viewModel.categories.value)
    }
}