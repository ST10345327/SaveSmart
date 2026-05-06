package com.example.savesmart.ui.reports

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SaveSmartRepository
    private lateinit var viewModel: ReportsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = ReportsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCategoryReport updates month display`() = runTest {
        viewModel.loadCategoryReport(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull(viewModel.currentMonthDisplay.value)
    }

    @Test
    fun `nextMonth changes the month display`() = runTest {
        val initialMonth = viewModel.currentMonthDisplay.value
        viewModel.nextMonth()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val nextMonth = viewModel.currentMonthDisplay.value
        org.junit.Assert.assertNotEquals(initialMonth, nextMonth)
    }
}