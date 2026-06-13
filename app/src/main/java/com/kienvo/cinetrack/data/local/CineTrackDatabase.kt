package com.kienvo.cinetrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kienvo.cinetrack.data.local.dao.MovieDao
import com.kienvo.cinetrack.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 3, exportSchema = false)
abstract class CineTrackDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}