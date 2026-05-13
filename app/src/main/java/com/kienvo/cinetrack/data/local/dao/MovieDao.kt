package com.kienvo.cinetrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kienvo.cinetrack.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object): Interface định nghĩa các phương thức để thao tác với cơ sở dữ liệu.
// Học thuật: DAO nằm ở tầng Data Layer, đóng vai trò như một cầu nối giữa mã Kotlin và SQLite. Room sẽ tự động sinh code thực thi (Implementation) cho các hàm này lúc compile.
// Đời thường: Đây là "người thủ thư" trong thư viện (database) cục bộ của bạn. Bạn không cần tự trèo lên lấy sách, chỉ cần đưa yêu cầu cho thủ thư (như "Thêm sách", "Tìm sách"), họ sẽ làm thay bạn.
@Dao
interface MovieDao {

    // Học thuật: Dùng câu lệnh SQL thuần để lấy toàn bộ phim. Trả về Flow để hỗ trợ khả năng Reactive (phản ứng) – nếu database thay đổi, UI sẽ tự động được cập nhật mà không cần truy vấn lại.
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<MovieEntity>>

    // Lọc ra các phim có isWatched = 0 (chưa xem).
    @Query("SELECT * FROM watchlist WHERE isWatched = 0 ORDER BY addedAt DESC")
    fun getWantToWatch(): Flow<List<MovieEntity>>

    // Lọc ra các phim có isWatched = 1 (đã xem).
    @Query("SELECT * FROM watchlist WHERE isWatched = 1 ORDER BY addedAt DESC")
    fun getWatched(): Flow<List<MovieEntity>>

    // Kiểm tra xem phim đã tồn tại trong database hay chưa. Trả ra Flow<Boolean>.
    // Đời thường: Hỏi thủ thư xem cuốn sách ID này đã nằm trong kho chưa (True/False).
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    fun isInWatchlist(movieId: Int): Flow<Boolean>

    // Hàm chèn (Thêm) một bộ phim vào database.
    // Học thuật: OnConflictStrategy.REPLACE sẽ ghi đè lên dòng dữ liệu cũ nếu ID (khoá chính) đã tồn tại (giúp cập nhật thông tin dễ dàng tránh crash do duplicate key).
    // Từ khoá suspend: Hàm này mất thời gian chạy (I/O operation) nên phải chạy bất đồng bộ (trên nền).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    // Hàm xoá một bộ phim khỏi database.
    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    // Hàm cập nhật trạng thái "đã xem".
    // :isWatched và :movieId là cách truyền tham số Kotlin vào câu lệnh SQL của Room.
    @Query("UPDATE watchlist SET isWatched = :isWatched WHERE id = :movieId")
    suspend fun updateWatchedStatus(movieId: Int, isWatched: Boolean)
}