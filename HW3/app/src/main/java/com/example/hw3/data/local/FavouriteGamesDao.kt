package com.example.hw3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteGamesDao {

    @Query("SELECT * FROM favourite_games ORDER BY title")
    fun observeAll(): Flow<List<FavouriteGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: FavouriteGameEntity)

    @Query("DELETE FROM favourite_games WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_games WHERE id = :id)")
    suspend fun isFavourite(id: Int): Boolean
}
