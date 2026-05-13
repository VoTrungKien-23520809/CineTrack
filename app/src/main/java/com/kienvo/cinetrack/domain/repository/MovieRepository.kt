package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.Movie
import kotlinx.coroutines.flow.Flow

// Repository Interface: Bản hợp đồng (Contract) cốt lõi định nghĩa ứng dụng có thể làm được những việc gì.
// Học thuật: Thuộc tầng Domain (tầng lõi trong Clean Architecture). Nó sử dụng nguyên lý Dependency Inversion (Đảo ngược sự phụ thuộc) - các tầng dữ liệu cấp thấp (như Room, Retrofit) phải phục Tùng và triển khai theo đúng giao kèo do tầng Domain này đưa ra.
// Đời thường: Đây là "bản mô tả công việc" cho ông quản lý kho (MovieRepositoryImpl). Không cần biết ông ta sẽ tải phim từ internet hay lấy từ máy, miễn là ông ta phải trả ra đúng các tính năng (hàm) đã ghi trong tờ giấy này.
interface MovieRepository {
    // API (Các hàm liên quan tới kết nối mạng)
    // Result<T>: Trả về kết quả Thành công (kèm dữ liệu) hoặc Thất bại (kèm Exception). Giúp app không bị crash.
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getTopRatedMovies(): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<Movie>

    // Watchlist (Các hàm liên quan tới Database cục bộ)
    // Flow<T>: Trả về luồng dữ liệu thay đổi liên tục theo thời gian thực.
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