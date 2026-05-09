package com.example.hw3

import com.example.hw3.data.local.CachedGameDao
import com.example.hw3.data.local.CachedGameEntity

class FakeCachedGameDao : CachedGameDao {
    private val store = mutableListOf<CachedGameEntity>()

    override suspend fun getAll(): List<CachedGameEntity> = store.toList()

    override suspend fun insertAll(games: List<CachedGameEntity>) {
        games.forEach { new ->
            store.removeIf { it.id == new.id }
            store.add(new)
        }
    }

    override suspend fun deleteAll() = store.clear()

    override suspend fun oldestCacheTime(): Long? = store.minOfOrNull { it.cachedAt }
}
