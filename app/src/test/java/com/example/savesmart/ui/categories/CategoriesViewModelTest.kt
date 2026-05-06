package com.example.savesmart.ui.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.savesmart.data.entity.Category
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.coVerify
import io.mockk.mockk
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
        repository = mockk(relaxed = true)
        viewModel = CategoriesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveCategory calls repository insertCategory`() = runTest {
        val category = Category(categoryId = 0, userId = 1, name = "Bills", color = "#000000", minGoalMilliunits = 100, maxGoalMilliunits = 50)
        
        viewModel.saveCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.insertCategory(category) }
    }

    @Test
    fun `deleteCategory calls repository deleteCategory`() = runTest {
        val categoryId = 1
        
        viewModel.deleteCategory(categoryId)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteCategory(categoryId) }
    }
}
