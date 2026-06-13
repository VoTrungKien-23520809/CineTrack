package com.kienvo.cinetrack.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String
) {
    fun fullPosterUrl() = "https://image.tmdb.org/t/p/w500$posterPath"
    fun fullBackdropUrl() = "https://image.tmdb.org/t/p/w780$backdropPath"
    fun formattedRating() = String.format("%.1f", voteAverage)
}