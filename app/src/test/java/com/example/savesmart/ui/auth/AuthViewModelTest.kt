package com.example.savesmart.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.util.Log
import com.example.savesmart.data.entity.User
import com.example.savesmart.data.repository.SaveSmartRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SaveSmartRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = mockk()
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty credentials returns error`() = runTest {
        viewModel.login("", "")
        assertTrue(viewModel.authState.value is AuthResult.Error)
    }

    @Test
    fun `login with valid credentials returns success`() = runTest {
        val user = User(userId = 1, username = "testuser", passwordHash = "hash", fullName = "Test User")
        
        // Use any() for the hashed password since it's computed in the VM
        coEvery { repository.loginUser("testuser", any()) } returns user

        viewModel.login("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthResult.Success)
    }

    @Test
    fun `register with invalid username returns error`() = runTest {
        viewModel.register("a", "password123", "password123")
        assertTrue(viewModel.authState.value is AuthResult.Error)
    }
}