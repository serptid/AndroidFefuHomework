package com.example.hw3

import com.example.hw3.data.local.CachedGameDetailDao
import com.example.hw3.data.local.CachedGameDetailEntity

class FakeCachedGameDetailDao : CachedGameDetailDao {
    private val store = mutableMapOf<Int, CachedGameDetailEntity>()

    override suspend fun getById(id: Int): CachedGameDetailEntity? = store[id]

    override suspend fun insert(entity: CachedGameDetailEntity) {
        store[entity.id] = entity
    }

    override suspend fun deleteAll() = store.clear()
}
