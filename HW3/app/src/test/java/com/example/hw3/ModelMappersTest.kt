package com.example.hw3

import com.example.hw3.data.GameDetailDto
import com.example.hw3.data.GameDto
import com.example.hw3.data.MinSystemRequirementsDto
import com.example.hw3.data.ScreenshotDto
import com.example.hw3.data.toGame
import com.example.hw3.data.toGameDetail
import com.example.hw3.data.local.FavouriteGameEntity
import com.example.hw3.data.local.toGame
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelMappersTest {

    @Test
    fun gameDtoToGame_mapsAllFieldsCorrectly() {
        val dto = GameDto(
            id = 42,
            title = "Warframe",
            thumbnail = "https://example.com/warframe.jpg",
            shortDescription = "A free-to-play action game",
            genre = "Action",
            platform = "PC",
            releaseDate = "2013-03-25"
        )

        val game = dto.toGame()

        assertEquals(dto.id, game.id)
        assertEquals(dto.title, game.title)
        assertEquals(dto.thumbnail, game.thumbnail)
        assertEquals(dto.shortDescription, game.shortDescription)
        assertEquals(dto.genre, game.genre)
        assertEquals(dto.platform, game.platform)
        assertEquals(dto.releaseDate, game.releaseDate)
    }

    @Test
    fun gameDetailDtoToGameDetail_mapsAllFieldsCorrectly() {
        val screenshotDto = ScreenshotDto(id = 1, image = "https://example.com/screen.jpg")
        val minReqDto = MinSystemRequirementsDto(
            os = "Windows 7",
            processor = "Intel Core i5",
            memory = "4 GB RAM",
            graphics = "GeForce GTX 780",
            storage = "15 GB"
        )
        val dto = GameDetailDto(
            id = 100,
            title = "Path of Exile",
            thumbnail = "https://example.com/poe.jpg",
            description = "An isometric action RPG",
            genre = "RPG",
            platform = "PC",
            releaseDate = "2013-10-23",
            publisher = "Grinding Gear Games",
            developer = "Grinding Gear Games",
            gameUrl = "https://www.pathofexile.com",
            screenshots = listOf(screenshotDto),
            minSystemRequirements = minReqDto
        )

        val detail = dto.toGameDetail()

        assertEquals(dto.id, detail.id)
        assertEquals(dto.title, detail.title)
        assertEquals(dto.thumbnail, detail.thumbnail)
        assertEquals(dto.description, detail.description)
        assertEquals(dto.genre, detail.genre)
        assertEquals(dto.platform, detail.platform)
        assertEquals(dto.releaseDate, detail.releaseDate)
        assertEquals(dto.publisher, detail.publisher)
        assertEquals(dto.developer, detail.developer)
        assertEquals(dto.gameUrl, detail.gameUrl)
        assertEquals(1, detail.screenshots.size)
        assertEquals(screenshotDto.id, detail.screenshots[0].id)
        assertEquals(screenshotDto.image, detail.screenshots[0].image)
        assertEquals(minReqDto.os, detail.minSystemRequirements?.os)
        assertEquals(minReqDto.processor, detail.minSystemRequirements?.processor)
        assertEquals(minReqDto.memory, detail.minSystemRequirements?.memory)
        assertEquals(minReqDto.graphics, detail.minSystemRequirements?.graphics)
        assertEquals(minReqDto.storage, detail.minSystemRequirements?.storage)
    }

    @Test
    fun toGame_fromEntity_mapsAllFields() {
        val entity = FavouriteGameEntity(
            id = 7,
            title = "Apex Legends",
            thumbnail = "https://example.com/apex.jpg",
            shortDescription = "A battle royale game",
            genre = "Battle Royale",
            platform = "PC",
            releaseDate = "2019-02-04"
        )

        val game = entity.toGame()

        assertEquals(entity.id, game.id)
        assertEquals(entity.title, game.title)
        assertEquals(entity.thumbnail, game.thumbnail)
        assertEquals(entity.shortDescription, game.shortDescription)
        assertEquals(entity.genre, game.genre)
        assertEquals(entity.platform, game.platform)
        assertEquals(entity.releaseDate, game.releaseDate)
    }
}
