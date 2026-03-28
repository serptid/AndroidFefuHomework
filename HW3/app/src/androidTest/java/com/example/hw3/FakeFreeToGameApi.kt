package com.example.hw3

import com.example.hw3.data.FreeToGameApi
import com.example.hw3.data.GameDetailDto
import com.example.hw3.data.GameDto

class FakeFreeToGameApi : FreeToGameApi {

    var gamesResult: List<GameDto> = emptyList()

    override suspend fun getGames(): List<GameDto> = gamesResult

    override suspend fun getGameDetail(id: Int): GameDetailDto =
        error("getGameDetail not used in integration tests")
}
