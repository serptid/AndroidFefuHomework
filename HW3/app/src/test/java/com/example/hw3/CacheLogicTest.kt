package com.example.hw3

import com.example.hw3.data.GameDetailDto
import com.example.hw3.data.GameDto
import com.example.hw3.data.GamesRepositoryImpl
import com.example.hw3.data.local.CachedGameEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CacheLogicTest {

    private lateinit var fakeApi: FakeFreeToGameApi
    private lateinit var fakeFavDao: FakeFavouriteGamesDao
    private lateinit var fakeCachedDao: FakeCachedGameDao
    private lateinit var fakeCachedDetailDao: FakeCachedGameDetailDao
    private lateinit var repository: GamesRepositoryImpl

    private val gameDto = GameDto(
        id = 1, title = "Cached Game", thumbnail = "t",
        shortDescription = "d", genre = "Action", platform = "PC", releaseDate = "2020-01-01"
    )

    @Before
    fun setUp() {
        fakeApi = FakeFreeToGameApi()
        fakeFavDao = FakeFavouriteGamesDao()
        fakeCachedDao = FakeCachedGameDao()
        fakeCachedDetailDao = FakeCachedGameDetailDao()
        repository = GamesRepositoryImpl(fakeApi, fakeFavDao, fakeCachedDao, fakeCachedDetailDao)
    }

    @Test
    fun getGames_withFreshCache_returnsCacheWithoutApiCall() = runTest {
        fakeCachedDao.insertAll(listOf(
            CachedGameEntity(1, "Cached Game", "t", "d", "Action", "PC", "2020-01-01",
                cachedAt = System.currentTimeMillis())
        ))
        fakeApi.gamesResult = listOf(gameDto.copy(title = "API Game"))

        val result = repository.getGames(cacheTtlHours = 24)

        assertEquals(1, result.size)
        assertEquals("Cached Game", result[0].title)
        assertEquals(0, fakeApi.getGamesCallCount)
    }

    @Test
    fun getGames_withStaleCache_hitsApiAndUpdatesCache() = runTest {
        val staleTime = System.currentTimeMillis() - 25 * 3_600_000L
        fakeCachedDao.insertAll(listOf(
            CachedGameEntity(1, "Stale Game", "t", "d", "Action", "PC", "2020-01-01",
                cachedAt = staleTime)
        ))
        fakeApi.gamesResult = listOf(gameDto.copy(title = "Fresh API Game"))

        val result = repository.getGames(cacheTtlHours = 24)

        assertEquals("Fresh API Game", result[0].title)
        assertEquals(1, fakeApi.getGamesCallCount)
    }

    @Test
    fun getGames_apiFailsWithStaleCache_returnsStaleCacheAsFallback() = runTest {
        val staleTime = System.currentTimeMillis() - 25 * 3_600_000L
        fakeCachedDao.insertAll(listOf(
            CachedGameEntity(1, "Stale Fallback", "t", "d", "Action", "PC", "2020-01-01",
                cachedAt = staleTime)
        ))
        fakeApi.throwOnGetGames = IOException("No network")

        val result = repository.getGames(cacheTtlHours = 24)

        assertEquals(1, result.size)
        assertEquals("Stale Fallback", result[0].title)
    }

    @Test
    fun getGames_apiFailsWithEmptyCache_throwsException() = runTest {
        fakeApi.throwOnGetGames = IOException("No network")

        try {
            repository.getGames(cacheTtlHours = 24)
            fail("Expected IOException")
        } catch (e: IOException) {
            assertEquals("No network", e.message)
        }
    }

    @Test
    fun forceRefreshGames_alwaysHitsApiAndUpdatesCache() = runTest {
        fakeCachedDao.insertAll(listOf(
            CachedGameEntity(1, "Old Cache", "t", "d", "Action", "PC", "2020-01-01",
                cachedAt = System.currentTimeMillis())
        ))
        fakeApi.gamesResult = listOf(gameDto.copy(title = "Forced Fresh"))

        val result = repository.forceRefreshGames()

        assertEquals("Forced Fresh", result[0].title)
        assertEquals(1, fakeApi.getGamesCallCount)
        assertEquals("Forced Fresh", fakeCachedDao.getAll()[0].title)
    }

    private val detailDto = GameDetailDto(
        id = 1, title = "Warframe", thumbnail = "t", description = "desc",
        genre = "Action", platform = "PC", releaseDate = "2020-01-01",
        publisher = "pub", developer = "dev", gameUrl = "https://example.com"
    )

    @Test
    fun getGameDetail_firstTime_hitsApiAndStoresInCache() = runTest {
        fakeApi.gameDetailResult = detailDto

        val result = repository.getGameDetail(1)

        assertEquals("Warframe", result.title)
        assertEquals(1, fakeApi.getDetailCallCount)
        assertNotNull(fakeCachedDetailDao.getById(1))
    }

    @Test
    fun getGameDetail_secondTime_returnsCachedResultWithoutApiCall() = runTest {
        fakeApi.gameDetailResult = detailDto
        repository.getGameDetail(1)
        fakeApi.getDetailCallCount = 0

        val result = repository.getGameDetail(1)

        assertEquals("Warframe", result.title)
        assertEquals(0, fakeApi.getDetailCallCount)
    }

    @Test
    fun clearGamesCache_alsoClearsDetailCache() = runTest {
        fakeApi.gameDetailResult = detailDto
        repository.getGameDetail(1)
        assertNotNull(fakeCachedDetailDao.getById(1))

        repository.clearGamesCache()

        assertNull(fakeCachedDetailDao.getById(1))
    }
}
