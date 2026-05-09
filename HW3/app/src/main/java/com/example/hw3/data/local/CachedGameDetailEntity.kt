package com.example.hw3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hw3.data.GameDetail
import com.example.hw3.data.Screenshot

@Entity(tableName = "cached_game_details")
data class CachedGameDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val thumbnail: String,
    val description: String,
    val genre: String,
    val platform: String,
    val releaseDate: String,
    val publisher: String,
    val developer: String,
    val gameUrl: String,
    val screenshotUrls: String,
    val cachedAt: Long
)

fun CachedGameDetailEntity.toGameDetail(): GameDetail {
    val screenshots = if (screenshotUrls.isBlank()) emptyList()
    else screenshotUrls.split("|").mapIndexed { index, url -> Screenshot(id = index, image = url) }
    return GameDetail(
        id = id, title = title, thumbnail = thumbnail, description = description,
        genre = genre, platform = platform, releaseDate = releaseDate,
        publisher = publisher, developer = developer, gameUrl = gameUrl,
        screenshots = screenshots
    )
}

fun GameDetail.toCachedDetailEntity(cachedAt: Long): CachedGameDetailEntity =
    CachedGameDetailEntity(
        id = id, title = title, thumbnail = thumbnail, description = description,
        genre = genre, platform = platform, releaseDate = releaseDate,
        publisher = publisher, developer = developer, gameUrl = gameUrl,
        screenshotUrls = screenshots.joinToString("|") { it.image },
        cachedAt = cachedAt
    )
