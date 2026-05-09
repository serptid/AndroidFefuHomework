package com.example.hw3

import app.cash.turbine.test
import com.example.hw3.data.HistoryRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {

    private lateinit var dao: FakeGameHistoryDao
    private lateinit var repository: HistoryRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeGameHistoryDao()
        repository = HistoryRepositoryImpl(dao)
    }

    @Test
    fun addToHistory_newEntry_appearsInFlow() = runTest {
        repository.addToHistory(1, "Game One", "t", "Action", "PC", maxSize = 50)

        repository.observeHistory().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Game One", list[0].title)
            assertEquals(1, list[0].viewCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addToHistory_sameGame_incrementsViewCount() = runTest {
        repository.addToHistory(1, "Game One", "t", "Action", "PC", maxSize = 50)
        repository.addToHistory(1, "Game One", "t", "Action", "PC", maxSize = 50)

        repository.observeHistory().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(2, list[0].viewCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addToHistory_exceedsMaxSize_trimsToBound() = runTest {
        repeat(5) { i ->
            repository.addToHistory(i, "Game $i", "t", "Action", "PC", maxSize = 3)
        }

        assertEquals(3, dao.size())
    }

    @Test
    fun clearHistory_removesAllEntries() = runTest {
        repository.addToHistory(1, "Game One", "t", "Action", "PC", maxSize = 50)
        repository.clearHistory()

        repository.observeHistory().test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeFromHistory_removesSpecificEntry() = runTest {
        repository.addToHistory(1, "Game One", "t", "Action", "PC", maxSize = 50)
        repository.addToHistory(2, "Game Two", "t", "RPG", "PC", maxSize = 50)

        repository.removeFromHistory(1)

        repository.observeHistory().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(2, list[0].gameId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
