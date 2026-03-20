package com.example.hw3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hw3.data.Game

@Entity(tableName = "favourite_games")
data class FavouriteGameEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val genre: String,
    val platform: String,
    val releaseDate: String
)

fun FavouriteGameEntity.toGame(): Game = Game(
    id = id,
    title = title,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate
)

fun Game.toEntity(): FavouriteGameEntity = FavouriteGameEntity(
    id = id,
    title = title,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate
)
