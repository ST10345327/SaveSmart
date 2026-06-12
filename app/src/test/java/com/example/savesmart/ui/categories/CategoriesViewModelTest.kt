package com.example.savesmart.ui.categories

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.coVerify
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SaveSmartRepository
    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        repository = mockk(relaxed = true)
        viewModel = CategoriesViewModel(repository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `saveCategory calls repository insertCategory when id is 0`() = runTest {
        val category = Category(categoryId = 0, userId = 1, name = "Bills", minGoalMilliunits = 100, maxGoalMilliunits = 50)
        
        viewModel.saveCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.insertCategory(category) }
    }

    @Test
    fun `saveCategory calls repository updateCategory when id is not 0`() = runTest {
        val category = Category(categoryId = 1, userId = 1, name = "Rent", minGoalMilliunits = 500, maxGoalMilliunits = 1000)
        
        viewModel.saveCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.updateCategory(category) }
    }

    @Test
    fun `deleteCategory calls repository deleteCategory`() = runTest {
        val categoryId = 1
        
        viewModel.deleteCategory(categoryId)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteCategory(categoryId) }
    }
}