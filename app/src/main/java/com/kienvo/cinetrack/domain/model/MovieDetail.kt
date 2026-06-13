package com.kienvo.cinetrack.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val runtime: Int?,
    val genres: List<String>
) {
    fun fullPosterUrl() = "https://image.tmdb.org/t/p/w500$posterPath"
    fun fullBackdropUrl() = "https://image.tmdb.org/t/p/w780$backdropPath"
    fun formattedRating() = String.format("%.1f", voteAverage)

    // "2h 14m" / "45m"
    fun formattedRuntime(): String? = runtime?.takeIf { it > 0 }?.let { total ->
        val hours = total / 60
        val minutes = total % 60
        if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    fun toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        releaseDate = releaseDate
    )
}
