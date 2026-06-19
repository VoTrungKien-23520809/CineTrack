package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.CastMember
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.model.MovieDetail
import com.kienvo.cinetrack.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<MovieDetail>
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>>
    suspend fun getMovieCredits(id: Int): Result<List<CastMember>>
    suspend fun getMovieVideos(id: Int): Result<List<Video>>
    suspend fun getRecommendations(id: Int): Result<List<Movie>>
    fun getWatchlist(): Flow<List<Movie>>
    fun getWantToWatch(): Flow<List<Movie>>
    fun getWatched(): Flow<List<Movie>>
    fun isInWatchlist(movieId: Int): Flow<Boolean>
    fun getWatchlistEntry(movieId: Int): Flow<Movie?>
    suspend fun addToWatchlist(movie: Movie)
    suspend fun removeFromWatchlist(movie: Movie)
    suspend fun markAsWatched(movieId: Int, isWatched: Boolean)
    suspend fun updateRatingAndNote(movieId: Int, rating: Int?, note: String?)
}