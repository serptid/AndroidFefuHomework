package com.example.hw3

import com.example.hw3.data.HistoryRepository
import com.example.hw3.data.local.GameHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeHistoryRepository : HistoryRepository {
    val historyFlow = MutableStateFlow<List<GameHistoryEntity>>(emptyList())

    override fun observeHistory(): Flow<List<GameHistoryEntity>> = historyFlow
    override suspend fun addToHistory(gameId: Int, title: String, thumbnail: String, genre: String, platform: String, maxSize: Int) {}
    override suspend fun clearHistory() { historyFlow.value = emptyList() }
    override suspend fun removeFromHistory(gameId: Int) {}
}
