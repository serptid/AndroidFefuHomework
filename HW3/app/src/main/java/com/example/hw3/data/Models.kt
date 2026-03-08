package com.example.hw3.data

import com.google.gson.annotations.SerializedName



data class GameDto(
    val id: Int,
    val title: String,
    val thumbnail: String,
    @SerializedName("short_description") val shortDescription: String,
    val genre: String,
    val platform: String,
    @SerializedName("release_date") val releaseDate: String
)

data class GameDetailDto(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val description: String,
    val genre: String,
    val platform: String,
    @SerializedName("release_date") val releaseDate: String,
    val publisher: String,
    val developer: String,
    @SerializedName("game_url") val gameUrl: String,
    val screenshots: List<ScreenshotDto> = emptyList(),
    @SerializedName("minimum_system_requirements")
    val minSystemRequirements: MinSystemRequirementsDto? = null
)

data class ScreenshotDto(
    val id: Int,
    val image: String
)

data class MinSystemRequirementsDto(
    val os: String? = null,
    val processor: String? = null,
    val memory: String? = null,
    val graphics: String? = null,
    val storage: String? = null
)


data class Game(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val genre: String,
    val platform: String,
    val releaseDate: String
)

data class GameDetail(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val description: String,
    val genre: String,
    val platform: String,
    val releaseDate: String,
    val publisher: String,
    val developer: String,
    val gameUrl: String,
    val screenshots: List<Screenshot> = emptyList(),
    val minSystemRequirements: MinSystemRequirements? = null
)

data class Screenshot(
    val id: Int,
    val image: String
)

data class MinSystemRequirements(
    val os: String?,
    val processor: String?,
    val memory: String?,
    val graphics: String?,
    val storage: String?
)


fun GameDto.toGame(): Game = Game(
    id = id,
    title = title,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate
)

fun GameDetailDto.toGameDetail(): GameDetail = GameDetail(
    id = id,
    title = title,
    thumbnail = thumbnail,
    description = description,
    genre = genre,
    platform = platform,
    releaseDate = releaseDate,
    publisher = publisher,
    developer = developer,
    gameUrl = gameUrl,
    screenshots = screenshots.map { it.toScreenshot() },
    minSystemRequirements = minSystemRequirements?.toMinReq()
)

fun ScreenshotDto.toScreenshot(): Screenshot = Screenshot(
    id = id,
    image = image
)

fun MinSystemRequirementsDto.toMinReq(): MinSystemRequirements = MinSystemRequirements(
    os = os,
    processor = processor,
    memory = memory,
    graphics = graphics,
    storage = storage
)
