package com.example.hw3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hw3.data.Game

@Entity(tableName = "cached_games")
data class CachedGameEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val genre: String,
    val platform: String,
    val releaseDate: String,
    val cachedAt: Long
)

fun CachedGameEntity.toGame(): Game = Game(
    id = id,
    title = title,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate
)

fun Game.toCachedEntity(cachedAt: Long): CachedGameEntity = CachedGameEntity(
    id = id,
    title = title,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate,
    cachedAt = cachedAt
)
