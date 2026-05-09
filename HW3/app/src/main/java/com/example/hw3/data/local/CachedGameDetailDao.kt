package com.example.hw3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedGameDetailDao {
    @Query("SELECT * FROM cached_game_details WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CachedGameDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CachedGameDetailEntity)

    @Query("DELETE FROM cached_game_details")
    suspend fun deleteAll()
}
