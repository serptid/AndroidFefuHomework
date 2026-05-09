package com.example.hw3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGameStatusDao {

    @Query("SELECT * FROM user_game_status WHERE gameId = :id")
    fun observeStatus(id: Int): Flow<UserGameStatusEntity?>

    @Query("SELECT * FROM user_game_status ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<UserGameStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserGameStatusEntity)

    @Query("DELETE FROM user_game_status WHERE gameId = :id")
    suspend fun delete(id: Int)
}
