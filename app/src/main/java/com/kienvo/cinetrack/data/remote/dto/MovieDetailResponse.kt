package com.kienvo.cinetrack.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kienvo.cinetrack.domain.model.Movie


data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String,

    val runtime: Int?,


    val genres: List<GenreDto>
) {
    fun toDomain() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        voteAverage = voteAverage, releaseDate = releaseDate
    )
}

data class GenreDto(val id: Int, val name: String)