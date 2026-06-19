package com.kienvo.cinetrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kienvo.cinetrack.data.local.dao.MovieDao
import com.kienvo.cinetrack.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 4, exportSchema = false)
abstract class CineTrackDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE watchlist ADD COLUMN userRating INTEGER")
                database.execSQL("ALTER TABLE watchlist ADD COLUMN note TEXT")
            }
        }
    }
}