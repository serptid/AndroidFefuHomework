package com.example.hw3.data

import com.example.hw3.data.local.GameHistoryDao
import com.example.hw3.data.local.GameHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryRepository {
    fun observeHistory(): Flow<List<GameHistoryEntity>>
    suspend fun addToHistory(gameId: Int, title: String, thumbnail: String, genre: String, platform: String, maxSize: Int)
    suspend fun clearHistory()
    suspend fun removeFromHistory(gameId: Int)
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: GameHistoryDao
) : HistoryRepository {

    override fun observeHistory(): Flow<List<GameHistoryEntity>> = dao.observeAll()

    override suspend fun addToHistory(
        gameId: Int,
        title: String,
        thumbnail: String,
        genre: String,
        platform: String,
        maxSize: Int
    ) {
        val existing = dao.getById(gameId)
        val entity = if (existing != null) {
            existing.copy(viewedAt = System.currentTimeMillis(), viewCount = existing.viewCount + 1)
        } else {
            GameHistoryEntity(gameId, title, thumbnail, genre, platform, System.currentTimeMillis(), 1)
        }
        dao.insert(entity)
        dao.trimToSize(maxSize)
    }

    override suspend fun clearHistory() = dao.deleteAll()

    override suspend fun removeFromHistory(gameId: Int) = dao.deleteById(gameId)
}
