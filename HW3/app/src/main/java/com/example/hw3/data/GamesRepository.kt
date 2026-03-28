package com.example.hw3.data

import com.example.hw3.data.local.FavouriteGamesDao
import com.example.hw3.data.local.toEntity
import com.example.hw3.data.local.toGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface GamesRepository {
    suspend fun getGames(): List<Game>
    suspend fun getGameDetail(id: Int): GameDetail
    fun getFavouriteGames(): Flow<List<Game>>
    suspend fun addFavourite(game: Game)
    suspend fun removeFavourite(id: Int)
    suspend fun isFavourite(id: Int): Boolean
}

@Singleton
class GamesRepositoryImpl @Inject constructor(
    private val api: FreeToGameApi,
    private val favouritesDao: FavouriteGamesDao
) : GamesRepository {

    override suspend fun getGames(): List<Game> =
        api.getGames().map { it.toGame() }

    override suspend fun getGameDetail(id: Int): GameDetail =
        api.getGameDetail(id).toGameDetail()

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
