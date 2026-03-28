package com.example.hw3

import com.example.hw3.data.local.FavouriteGameEntity
import com.example.hw3.data.local.FavouriteGamesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavouriteGamesDao : FavouriteGamesDao {

    private val store = mutableMapOf<Int, FavouriteGameEntity>()
    private val flow = MutableStateFlow<List<FavouriteGameEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavouriteGameEntity>> = flow

    override suspend fun insert(game: FavouriteGameEntity) {
        store[game.id] = game
        flow.value = store.values.sortedBy { it.title }
    }

    override suspend fun deleteById(id: Int) {
        store.remove(id)
        flow.value = store.values.sortedBy { it.title }
    }

    override suspend fun isFavourite(id: Int): Boolean = store.containsKey(id)
}
