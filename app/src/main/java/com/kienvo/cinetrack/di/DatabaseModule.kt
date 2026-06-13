package com.kienvo.cinetrack.di

import android.content.Context
import androidx.room.Room
import com.kienvo.cinetrack.data.local.CineTrackDatabase
import com.kienvo.cinetrack.data.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CineTrackDatabase =
        Room.databaseBuilder(
            context,
            CineTrackDatabase::class.java,
            "cinetrack_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMovieDao(database: CineTrackDatabase): MovieDao =
        database.movieDao()
}
