package com.example.hw3

import app.cash.turbine.test
import com.example.hw3.data.StatusRepositoryImpl
import com.example.hw3.data.local.UserGameStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class StatusRepositoryTest {

    private lateinit var dao: FakeUserGameStatusDao
    private lateinit var repository: StatusRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeUserGameStatusDao()
        repository = StatusRepositoryImpl(dao)
    }

    @Test
    fun setStatus_gameHasCorrectStatus() = runTest {
        repository.setStatus(1, "Game One", UserGameStatus.PLAYING)

        repository.observeStatus(1).test {
            assertEquals(UserGameStatus.PLAYING, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearStatus_gameHasNoStatus() = runTest {
        repository.setStatus(1, "Game One", UserGameStatus.PLAYING)
        repository.clearStatus(1)

        repository.observeStatus(1).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeAllStatuses_returnsCorrectMap() = runTest {
        repository.setStatus(1, "Game One", UserGameStatus.INTERESTED)
        repository.setStatus(2, "Game Two", UserGameStatus.DROPPED)

        repository.observeAllStatuses().test {
            val map = awaitItem()
            assertEquals(2, map.size)
            assertEquals(UserGameStatus.INTERESTED, map[1])
            assertEquals(UserGameStatus.DROPPED, map[2])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setStatus_updatesExistingStatus() = runTest {
        repository.setStatus(1, "Game One", UserGameStatus.INTERESTED)
        repository.setStatus(1, "Game One", UserGameStatus.PLAYED)

        repository.observeAllStatuses().test {
            val map = awaitItem()
            assertEquals(1, map.size)
            assertEquals(UserGameStatus.PLAYED, map[1])
            cancelAndIgnoreRemainingEvents()
        }
    }
}
