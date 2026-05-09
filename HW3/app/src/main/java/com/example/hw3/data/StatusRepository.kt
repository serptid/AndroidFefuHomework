package com.example.hw3.data

import com.example.hw3.data.local.UserGameStatus
import com.example.hw3.data.local.UserGameStatusDao
import com.example.hw3.data.local.UserGameStatusEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface StatusRepository {
    fun observeStatus(gameId: Int): Flow<UserGameStatus?>
    fun observeAllStatuses(): Flow<Map<Int, UserGameStatus>>
    suspend fun setStatus(gameId: Int, gameTitle: String, status: UserGameStatus)
    suspend fun clearStatus(gameId: Int)
}

@Singleton
class StatusRepositoryImpl @Inject constructor(
    private val dao: UserGameStatusDao
) : StatusRepository {

    override fun observeStatus(gameId: Int): Flow<UserGameStatus?> =
        dao.observeStatus(gameId).map { it?.status }

    override fun observeAllStatuses(): Flow<Map<Int, UserGameStatus>> =
        dao.observeAll().map { list -> list.associate { it.gameId to it.status } }

    override suspend fun setStatus(gameId: Int, gameTitle: String, status: UserGameStatus) {
        dao.upsert(UserGameStatusEntity(gameId, gameTitle, status, System.currentTimeMillis()))
    }

    override suspend fun clearStatus(gameId: Int) {
        dao.delete(gameId)
    }
}
