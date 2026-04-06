package com.example.hw3

import app.cash.turbine.test
import com.example.hw3.data.Game
import com.example.hw3.data.GameDto
import com.example.hw3.data.GamesRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class GamesRepositoryUnitTest {

    private lateinit var fakeApi: FakeFreeToGameApi
    private lateinit var fakeDao: FakeFavouriteGamesDao
    private lateinit var repository: GamesRepositoryImpl

    @Before
    fun setUp() {
        fakeApi = FakeFreeToGameApi()
        fakeDao = FakeFavouriteGamesDao()
        repository = GamesRepositoryImpl(fakeApi, fakeDao)
    }

    @Test
    fun getGames_returnsMappedDomainModels() = runTest {
        fakeApi.gamesResult = listOf(
            GameDto(
                id = 1,
                title = "Warframe",
                thumbnail = "https://example.com/wf.jpg",
                shortDescription = "Free-to-play action game",
                genre = "Action",
                platform = "PC",
                releaseDate = "2013-03-25"
            )
        )

        val games = repository.getGames()

        assertEquals(1, games.size)
        assertEquals(1, games[0].id)
        assertEquals("Warframe", games[0].title)
        assertEquals("Action", games[0].genre)
        assertEquals("PC", games[0].platform)
        assertEquals("2013-03-25", games[0].releaseDate)
    }

    @Test
    fun getGames_whenApiThrows_propagatesException() = runTest {
        fakeApi.throwOnGetGames = IOException("Network error")

        try {
            repository.getGames()
            fail("Expected IOException to be thrown")
        } catch (e: IOException) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun addFavourite_thenIsFavourite_returnsTrue() = runTest {
        val game = Game(
            id = 5, title = "Apex Legends", thumbnail = "t",
            shortDescription = "d", genre = "Battle Royale",
            platform = "PC", releaseDate = "2019-02-04"
        )

        repository.addFavourite(game)

        assertTrue(repository.isFavourite(5))
    }

    @Test
    fun addFavourite_twice_doesNotCreateDuplicate() = runTest {
        val game = Game(
            id = 5, title = "Apex Legends", thumbnail = "t",
            shortDescription = "d", genre = "Battle Royale",
            platform = "PC", releaseDate = "2019-02-04"
        )

        repository.addFavourite(game)
        repository.addFavourite(game)

        repository.getFavouriteGames().test {
            val list = awaitItem()
            assertEquals("Expected exactly 1 game, got ${list.size}", 1, list.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
