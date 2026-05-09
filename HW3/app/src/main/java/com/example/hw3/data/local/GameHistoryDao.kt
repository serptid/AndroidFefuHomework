package com.example.hw3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {

    @Query("SELECT * FROM game_history ORDER BY viewedAt DESC")
    fun observeAll(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history WHERE gameId = :id")
    suspend fun getById(id: Int): GameHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GameHistoryEntity)

    @Query("DELETE FROM game_history")
    suspend fun deleteAll()

    @Query("DELETE FROM game_history WHERE gameId = :id")
    suspend fun deleteById(id: Int)

    @Query(
        "DELETE FROM game_history WHERE gameId NOT IN " +
        "(SELECT gameId FROM game_history ORDER BY viewedAt DESC LIMIT :keepCount)"
    )
    suspend fun trimToSize(keepCount: Int)
}
