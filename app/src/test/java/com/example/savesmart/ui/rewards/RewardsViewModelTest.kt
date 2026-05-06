package com.example.savesmart.ui.rewards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.savesmart.data.entity.Badge
import com.example.savesmart.data.entity.User
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
class RewardsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SaveSmartRepository
    private lateinit var viewModel: RewardsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = RewardsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserData updates userData live data`() = runTest {
        val userId = 1
        val user = User(userId = userId, username = "testuser", passwordHash = "hash", fullName = "Test User", totalPoints = 500)
        val userLiveData = MutableLiveData<User?>(user)

        every { repository.getUserLive(userId) } returns userLiveData

        // switchMap requires observation to trigger
        viewModel.userData.observeForever {}

        viewModel.loadUserData(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(user, viewModel.userData.value)
    }

    @Test
    fun `getEarnedBadges returns live data from repository`() = runTest {
        val userId = 1
        val badges = listOf(Badge(1, "FIRST_SAVE", "First Save", "desc", "icon", 10))
        val badgeLiveData = MutableLiveData<List<Badge>>(badges)

        every { repository.getEarnedBadgesLive(userId) } returns badgeLiveData

        val result = viewModel.getEarnedBadges(userId)
        
        assertEquals(badges, result.value)
    }
}