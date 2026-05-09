package com.example.hw3

import com.example.hw3.data.local.UserGameStatus
import com.example.hw3.data.local.UserGameStatusDao
import com.example.hw3.data.local.UserGameStatusEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserGameStatusDao : UserGameStatusDao {
    private val store = mutableMapOf<Int, UserGameStatusEntity>()
    private val flow = MutableStateFlow<List<UserGameStatusEntity>>(emptyList())

    override fun observeStatus(id: Int): Flow<UserGameStatusEntity?> = flow.map { it.find { e -> e.gameId == id } }

    override fun observeAll(): Flow<List<UserGameStatusEntity>> = flow

    override suspend fun upsert(entity: UserGameStatusEntity) {
        store[entity.gameId] = entity
        flow.value = store.values.toList()
    }

    override suspend fun delete(id: Int) {
        store.remove(id)
        flow.value = store.values.toList()
    }
}
