package com.kienvo.cinetrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kienvo.cinetrack.domain.model.Movie


@Entity(tableName = "watchlist")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val userId: String,
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val isWatched: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

fun MovieEntity.toDomain() = Movie(
    id = id, title = title, overview = overview,
    posterPath = posterPath, backdropPath = backdropPath,
    voteAverage = voteAverage, releaseDate = releaseDate
)

fun Movie.toEntity(userId: String, isWatched: Boolean = false) = MovieEntity(
    userId = userId,   // thêm userId
    id = id, title = title, overview = overview,
    posterPath = posterPath, backdropPath = backdropPath,
    voteAverage = voteAverage, releaseDate = releaseDate,
    isWatched = isWatched
)