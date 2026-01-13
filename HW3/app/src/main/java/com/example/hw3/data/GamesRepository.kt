package com.example.hw3.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface GamesRepository {
    suspend fun getGames(): List<Game>
    suspend fun getGameDetail(id: Int): GameDetail
}

class GamesRepositoryImpl : GamesRepository {

    companion object {
        private val api: FreeToGameApi by lazy {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl("https://www.freetogame.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FreeToGameApi::class.java)
        }
    }

    override suspend fun getGames(): List<Game> =
        api.getGames().map { it.toGame() }

    override suspend fun getGameDetail(id: Int): GameDetail =
        api.getGameDetail(id).toGameDetail()
}
