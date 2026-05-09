package com.example.hw3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedGameDao {

    @Query("SELECT * FROM cached_games ORDER BY title")
    suspend fun getAll(): List<CachedGameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<CachedGameEntity>)

    @Query("DELETE FROM cached_games")
    suspend fun deleteAll()

    @Query("SELECT MIN(cachedAt) FROM cached_games")
    suspend fun oldestCacheTime(): Long?
}
