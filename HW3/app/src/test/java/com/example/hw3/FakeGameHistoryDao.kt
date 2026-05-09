package com.example.hw3

import com.example.hw3.data.local.GameHistoryDao
import com.example.hw3.data.local.GameHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGameHistoryDao : GameHistoryDao {
    private val store = mutableMapOf<Int, GameHistoryEntity>()
    private val flow = MutableStateFlow<List<GameHistoryEntity>>(emptyList())

    override fun observeAll(): Flow<List<GameHistoryEntity>> = flow

    override suspend fun getById(id: Int): GameHistoryEntity? = store[id]

    override suspend fun insert(entity: GameHistoryEntity) {
        store[entity.gameId] = entity
        flow.value = store.values.sortedByDescending { it.viewedAt }
    }

    override suspend fun deleteAll() {
        store.clear()
        flow.value = emptyList()
    }

    override suspend fun deleteById(id: Int) {
        store.remove(id)
        flow.value = store.values.sortedByDescending { it.viewedAt }
    }

    override suspend fun trimToSize(keepCount: Int) {
        val toKeep = store.values.sortedByDescending { it.viewedAt }.take(keepCount).map { it.gameId }.toSet()
        store.keys.retainAll(toKeep)
        flow.value = store.values.sortedByDescending { it.viewedAt }
    }

    fun size(): Int = store.size
}
