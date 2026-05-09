package com.example.hw3.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hw3.BuildConfig
import com.example.hw3.data.FreeToGameApi
import com.example.hw3.data.GamesRepository
import com.example.hw3.data.GamesRepositoryImpl
import com.example.hw3.data.HistoryRepository
import com.example.hw3.data.HistoryRepositoryImpl
import com.example.hw3.data.StatusRepository
import com.example.hw3.data.StatusRepositoryImpl
import com.example.hw3.data.local.AppDatabase
import com.example.hw3.data.local.CachedGameDao
import com.example.hw3.data.local.CachedGameDetailDao
import com.example.hw3.data.local.FavouriteGamesDao
import com.example.hw3.data.local.GameHistoryDao
import com.example.hw3.data.local.UserGameStatusDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `game_history` (" +
            "`gameId` INTEGER NOT NULL, " +
            "`title` TEXT NOT NULL, " +
            "`thumbnail` TEXT NOT NULL, " +
            "`genre` TEXT NOT NULL, " +
            "`platform` TEXT NOT NULL, " +
            "`viewedAt` INTEGER NOT NULL, " +
            "`viewCount` INTEGER NOT NULL, " +
            "PRIMARY KEY(`gameId`))"
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `cached_games` (" +
            "`id` INTEGER NOT NULL, " +
            "`title` TEXT NOT NULL, " +
            "`thumbnail` TEXT NOT NULL, " +
            "`shortDescription` TEXT NOT NULL, " +
            "`genre` TEXT NOT NULL, " +
            "`platform` TEXT NOT NULL, " +
            "`releaseDate` TEXT NOT NULL, " +
            "`cachedAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`id`))"
        )
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `user_game_status` (" +
            "`gameId` INTEGER NOT NULL, " +
            "`gameTitle` TEXT NOT NULL, " +
            "`status` TEXT NOT NULL, " +
            "`updatedAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`gameId`))"
        )
    }
}

private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `cached_game_details` (" +
            "`id` INTEGER NOT NULL, " +
            "`title` TEXT NOT NULL, " +
            "`thumbnail` TEXT NOT NULL, " +
            "`description` TEXT NOT NULL, " +
            "`genre` TEXT NOT NULL, " +
            "`platform` TEXT NOT NULL, " +
            "`releaseDate` TEXT NOT NULL, " +
            "`publisher` TEXT NOT NULL, " +
            "`developer` TEXT NOT NULL, " +
            "`gameUrl` TEXT NOT NULL, " +
            "`screenshotUrls` TEXT NOT NULL, " +
            "`cachedAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`id`))"
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.freetogame.com/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFreeToGameApi(retrofit: Retrofit): FreeToGameApi =
        retrofit.create(FreeToGameApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "games.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
        .build()

    @Provides
    @Singleton
    fun provideFavouriteGamesDao(database: AppDatabase): FavouriteGamesDao =
        database.favouriteGamesDao()

    @Provides
    @Singleton
    fun provideGameHistoryDao(database: AppDatabase): GameHistoryDao =
        database.gameHistoryDao()

    @Provides
    @Singleton
    fun provideCachedGameDao(database: AppDatabase): CachedGameDao =
        database.cachedGameDao()

    @Provides
    @Singleton
    fun provideUserGameStatusDao(database: AppDatabase): UserGameStatusDao =
        database.userGameStatusDao()

    @Provides
    @Singleton
    fun provideCachedGameDetailDao(database: AppDatabase): CachedGameDetailDao =
        database.cachedGameDetailDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("app_preferences") }
        )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        impl: GamesRepositoryImpl
    ): GamesRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindStatusRepository(
        impl: StatusRepositoryImpl
    ): StatusRepository
}
