package com.example.hw3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey val gameId: Int,
    val title: String,
    val thumbnail: String,
    val genre: String,
    val platform: String,
    val viewedAt: Long,
    val viewCount: Int
)
