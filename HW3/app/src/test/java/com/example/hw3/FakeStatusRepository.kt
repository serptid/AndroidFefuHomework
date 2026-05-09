package com.example.hw3

import com.example.hw3.data.StatusRepository
import com.example.hw3.data.local.UserGameStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeStatusRepository : StatusRepository {
    private val statusFlow = MutableStateFlow<Map<Int, UserGameStatus>>(emptyMap())

    override fun observeStatus(gameId: Int): Flow<UserGameStatus?> =
        statusFlow.map { it[gameId] }

    override fun observeAllStatuses(): Flow<Map<Int, UserGameStatus>> = statusFlow

    override suspend fun setStatus(gameId: Int, gameTitle: String, status: UserGameStatus) {
        statusFlow.value = statusFlow.value + (gameId to status)
    }

    override suspend fun clearStatus(gameId: Int) {
        statusFlow.value = statusFlow.value - gameId
    }
}
