package com.example.hw3.data

import com.example.hw3.data.local.CachedGameDao
import com.example.hw3.data.local.CachedGameDetailDao
import com.example.hw3.data.local.FavouriteGamesDao
import com.example.hw3.data.local.toCachedDetailEntity
import com.example.hw3.data.local.toCachedEntity
import com.example.hw3.data.local.toEntity
import com.example.hw3.data.local.toGame
import com.example.hw3.data.local.toGameDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface GamesRepository {
    suspend fun getGames(cacheTtlHours: Int = 24): List<Game>
    suspend fun forceRefreshGames(): List<Game>
    suspend fun getGameDetail(id: Int): GameDetail
    suspend fun clearGamesCache()
    fun getFavouriteGames(): Flow<List<Game>>
    suspend fun addFavourite(game: Game)
    suspend fun removeFavourite(id: Int)
    suspend fun isFavourite(id: Int): Boolean
}

@Singleton
class GamesRepositoryImpl @Inject constructor(
    private val api: FreeToGameApi,
    private val favouritesDao: FavouriteGamesDao,
    private val cachedGameDao: CachedGameDao,
    private val cachedGameDetailDao: CachedGameDetailDao
) : GamesRepository {

    override suspend fun getGames(cacheTtlHours: Int): List<Game> {
        val cachedEntities = cachedGameDao.getAll()
        if (cachedEntities.isNotEmpty()) {
            val oldestCachedAt = cachedGameDao.oldestCacheTime() ?: 0L
            val ttlMs = cacheTtlHours * 3_600_000L
            if (System.currentTimeMillis() - oldestCachedAt < ttlMs) {
                return cachedEntities.map { it.toGame() }
            }
        }
        return try {
            val games = api.getGames().map { it.toGame() }
            val now = System.currentTimeMillis()
            cachedGameDao.deleteAll()
            cachedGameDao.insertAll(games.map { it.toCachedEntity(now) })
            games
        } catch (e: Exception) {
            if (cachedEntities.isNotEmpty()) cachedEntities.map { it.toGame() }
            else throw e
        }
    }

    override suspend fun forceRefreshGames(): List<Game> {
        val games = api.getGames().map { it.toGame() }
        val now = System.currentTimeMillis()
        cachedGameDao.deleteAll()
        cachedGameDao.insertAll(games.map { it.toCachedEntity(now) })
        return games
    }

    override suspend fun getGameDetail(id: Int): GameDetail {
        val cached = cachedGameDetailDao.getById(id)
        if (cached != null) return cached.toGameDetail()
        val detail = api.getGameDetail(id).toGameDetail()
        cachedGameDetailDao.insert(detail.toCachedDetailEntity(System.currentTimeMillis()))
        return detail
    }

    override suspend fun clearGamesCache() {
        cachedGameDao.deleteAll()
        cachedGameDetailDao.deleteAll()
    }

    override fun getFavouriteGames(): Flow<List<Game>> =
        favouritesDao.observeAll().map { list -> list.map { it.toGame() } }

    override suspend fun addFavourite(game: Game) {
        favouritesDao.insert(game.toEntity())
    }

    override suspend fun removeFavourite(id: Int) {
        favouritesDao.deleteById(id)
    }

    override suspend fun isFavourite(id: Int): Boolean =
        favouritesDao.isFavourite(id)
}
