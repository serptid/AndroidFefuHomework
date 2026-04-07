package com.example.hw3.data

import retrofit2.http.GET
import retrofit2.http.Query

interface FreeToGameApi {

    @GET("games")
    suspend fun getGames(): List<GameDto>

    @GET("game")
    suspend fun getGameDetail(@Query("id") id: Int): GameDetailDto
}
