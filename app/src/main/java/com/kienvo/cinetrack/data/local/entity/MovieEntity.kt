package com.kienvo.cinetrack.data.local.entity

// Import thư viện Room của Google để làm việc với database SQLite trên Android dễ dàng hơn.
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kienvo.cinetrack.domain.model.Movie

// Data class đại diện cho một bảng (Table) trong database nội bộ (Local Database).
// Học thuật: Pattern ORM (Object-Relational Mapping). Annotation @Entity báo cho thư viện Room biết cần tạo một bảng tên là "watchlist" dựa trên cấu trúc data class này.
// Đời thường: Đây là chiếc hộp nhựa (dữ liệu thô) để cất vào ngăn tủ (database) trong nhà bạn. Tủ này tên là "watchlist".
@Entity(tableName = "watchlist")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val userId: String,           // thêm field này
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

// Extension function (Hàm mở rộng) trong Kotlin.
// Mapper function: Đóng vai trò làm cầu nối (Adapter) biến Model của Database (Entity) thành Model chung toàn cục (Domain).
// Học thuật: Tương tự như DTO phía Network, Entity cũng cần được cô lập. Trả về Domain Model giúp che giấu chi tiết cài đặt Database (như bảng tên gì, có cột addedAt hay isWatched) với tầng phía trên.
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