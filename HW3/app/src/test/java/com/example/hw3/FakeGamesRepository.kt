package com.example.hw3

import com.example.hw3.data.Game
import com.example.hw3.data.GameDetail
import com.example.hw3.data.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGamesRepository : GamesRepository {

    var gamesResult: Result<List<Game>> = Result.success(emptyList())
    var gameDetailResult: Result<GameDetail> = Result.failure(NotImplementedError())
    val favouritesFlow = MutableStateFlow<List<Game>>(emptyList())

    var getGamesCallCount = 0
    var getDetailCallCount = 0
    var addFavouriteCallCount = 0
    var removeFavouriteCallCount = 0

    private val favouriteSet = mutableListOf<Game>()

    override suspend fun getGames(): List<Game> {
        getGamesCallCount++
        return gamesResult.getOrThrow()
    }

    override suspend fun getGameDetail(id: Int): GameDetail {
        getDetailCallCount++
        return gameDetailResult.getOrThrow()
    }

    override fun getFavouriteGames(): Flow<List<Game>> = favouritesFlow

    override suspend fun addFavourite(game: Game) {
        addFavouriteCallCount++
        favouriteSet.removeIf { it.id == game.id }
        favouriteSet.add(game)
        favouritesFlow.emit(favouriteSet.toList())
    }

    override suspend fun removeFavourite(id: Int) {
        removeFavouriteCallCount++
        favouriteSet.removeIf { it.id == id }
        favouritesFlow.emit(favouriteSet.toList())
    }

    override suspend fun isFavourite(id: Int): Boolean = favouriteSet.any { it.id == id }
}
