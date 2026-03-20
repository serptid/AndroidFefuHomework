package com.example.hw3

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.hw3.data.local.AppDatabase
import com.example.hw3.data.local.FavouriteGameEntity
import com.example.hw3.data.local.FavouriteGamesDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouritesDaoIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavouriteGamesDao

    private val testEntity = FavouriteGameEntity(
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
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favouriteGamesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insert_thenObserveAll_returnsCorrectData() = runTest {
        dao.insert(testEntity)

        dao.observeAll().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(testEntity.id, list[0].id)
            assertEquals(testEntity.title, list[0].title)
            assertEquals(testEntity.genre, list[0].genre)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun doubleInsert_noDuplicate() = runTest {
        dao.insert(testEntity)
        dao.insert(testEntity)

        dao.observeAll().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteById_removedFromFlow() = runTest {
        dao.observeAll().test {
            assertEquals(emptyList<FavouriteGameEntity>(), awaitItem())

            dao.insert(testEntity)
            assertEquals(1, awaitItem().size)

            dao.deleteById(testEntity.id)
            assertEquals(emptyList<FavouriteGameEntity>(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
