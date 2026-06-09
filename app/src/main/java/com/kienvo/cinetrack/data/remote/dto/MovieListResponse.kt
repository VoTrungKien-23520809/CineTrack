package com.kienvo.cinetrack.data.remote.dto


import com.google.gson.annotations.SerializedName
import com.kienvo.cinetrack.domain.model.Movie
data class MovieListResponse(
    val page: Int,
    val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String
) {
    fun toDomain() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        voteAverage = voteAverage, releaseDate = releaseDate
    )
}