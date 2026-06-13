package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.CastMember
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.model.MovieDetail
import com.kienvo.cinetrack.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getTopRatedMovies(): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<MovieDetail>
    suspend fun searchMovies(query: String): Result<List<Movie>>
    suspend fun getMovieCredits(id: Int): Result<List<CastMember>>
    suspend fun getMovieVideos(id: Int): Result<List<Video>>
    suspend fun getRecommendations(id: Int): Result<List<Movie>>
    fun getWatchlist(): Flow<List<Movie>>
    fun getWantToWatch(): Flow<List<Movie>>
    fun getWatched(): Flow<List<Movie>>

    // Kiểm tra xem ID phim này đã "Lưu" vào app chưa.
    fun isInWatchlist(movieId: Int): Flow<Boolean>

    // Thêm / Xoá phim vào database cục bộ.
    suspend fun addToWatchlist(movie: Movie)
    suspend fun removeFromWatchlist(movie: Movie)

    // Đánh dấu trạng thái đã xem của một phim.
    suspend fun markAsWatched(movieId: Int, isWatched: Boolean)
}