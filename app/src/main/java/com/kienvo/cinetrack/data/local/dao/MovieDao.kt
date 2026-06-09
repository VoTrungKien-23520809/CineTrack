package com.kienvo.cinetrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kienvo.cinetrack.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM watchlist WHERE userId = :userId ORDER BY addedAt DESC")
    fun getAllWatchlist(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM watchlist WHERE userId = :userId AND isWatched = 0 ORDER BY addedAt DESC")
    fun getWantToWatch(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM watchlist WHERE userId = :userId AND isWatched = 1 ORDER BY addedAt DESC")
    fun getWatched(userId: String): Flow<List<MovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE userId = :userId AND id = :movieId)")
    fun isInWatchlist(userId: String, movieId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("UPDATE watchlist SET isWatched = :isWatched WHERE userId = :userId AND id = :movieId")
    suspend fun updateWatchedStatus(userId: String, movieId: Int, isWatched: Boolean)
}