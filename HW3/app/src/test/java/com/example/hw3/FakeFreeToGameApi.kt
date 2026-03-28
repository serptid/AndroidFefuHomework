package com.example.hw3

import com.example.hw3.data.FreeToGameApi
import com.example.hw3.data.GameDetailDto
import com.example.hw3.data.GameDto

class FakeFreeToGameApi : FreeToGameApi {

    var gamesResult: List<GameDto> = emptyList()
    var gameDetailResult: GameDetailDto? = null
    var throwOnGetGames: Exception? = null

    override suspend fun getGames(): List<GameDto> {
        throwOnGetGames?.let { throw it }
        return gamesResult
    }

    override suspend fun getGameDetail(id: Int): GameDetailDto =
        gameDetailResult ?: error("gameDetailResult not configured")
}
