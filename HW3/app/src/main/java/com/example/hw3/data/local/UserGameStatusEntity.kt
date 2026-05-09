package com.example.hw3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

enum class UserGameStatus {
    INTERESTED, PLAYING, PLAYED, DROPPED
}

class StatusConverters {
    @TypeConverter
    fun fromStatus(status: UserGameStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): UserGameStatus = UserGameStatus.valueOf(value)
}

@Entity(tableName = "user_game_status")
@TypeConverters(StatusConverters::class)
data class UserGameStatusEntity(
    @PrimaryKey val gameId: Int,
    val gameTitle: String,
    val status: UserGameStatus,
    val updatedAt: Long
)
