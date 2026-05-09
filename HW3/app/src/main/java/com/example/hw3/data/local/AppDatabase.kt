package com.example.hw3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        FavouriteGameEntity::class,
        GameHistoryEntity::class,
        CachedGameEntity::class,
        UserGameStatusEntity::class,
        CachedGameDetailEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(StatusConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteGamesDao(): FavouriteGamesDao
    abstract fun gameHistoryDao(): GameHistoryDao
    abstract fun cachedGameDao(): CachedGameDao
    abstract fun userGameStatusDao(): UserGameStatusDao
    abstract fun cachedGameDetailDao(): CachedGameDetailDao
}
