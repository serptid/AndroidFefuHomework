package com.example.hw3

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepositoryImpl
import com.example.hw3.data.local.AppDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Интеграционный тест: GamesRepositoryImpl + реальная in-memory Room БД.
 * Проверяет контракт репозитория — слой, через который приложение реально работает с данными.
 */
@RunWith(AndroidJUnit4::class)
class GamesRepositoryIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: GamesRepositoryImpl

    private val testGame = Game(
        id = 1,
        title = "Test Game",
        thumbnail = "https://example.com/thumb.jpg",
        shortDescription = "A test game",
        genre = "Shooter",
        platform = "PC",
        releaseDate = "2023-01-01"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GamesRepositoryImpl(FakeFreeToGameApi(), database.favouriteGamesDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    // addFavourite → getFavouriteGames возвращает корректные данные через репозиторий
    @Test
    fun addFavourite_getFavouriteGames_returnsCorrectData() = runTest {
        repository.addFavourite(testGame)

        repository.getFavouriteGames().test {
            val games = awaitItem()
            assertEquals(1, games.size)
            assertEquals(testGame.id, games[0].id)
            assertEquals(testGame.title, games[0].title)
            assertEquals(testGame.genre, games[0].genre)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Повторный addFavourite не создаёт дубль в реальной БД (нетривиальный)
    @Test
    fun addFavourite_twice_noDuplicateInDatabase() = runTest {
        repository.addFavourite(testGame)
        repository.addFavourite(testGame)

        repository.getFavouriteGames().test {
            val games = awaitItem()
            assertEquals(1, games.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Turbine: полная последовательность эмиссий через репозиторий (нетривиальный Flow-тест)
    @Test
    fun addThenRemoveFavourite_flowEmitsCorrectSequence() = runTest {
        repository.getFavouriteGames().test {
            assertEquals(emptyList<Game>(), awaitItem())

            repository.addFavourite(testGame)
            assertEquals(1, awaitItem().size)

            repository.removeFavourite(testGame.id)
            assertEquals(emptyList<Game>(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
