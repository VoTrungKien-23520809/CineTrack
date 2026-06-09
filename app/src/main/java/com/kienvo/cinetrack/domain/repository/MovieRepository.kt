package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.Movie
import kotlinx.coroutines.flow.Flow
interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getTopRatedMovies(): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<Movie>
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