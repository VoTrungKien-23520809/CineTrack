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
    // Khoá chính (Primary Key).
    // Học thuật: Mọi dòng dữ liệu (Row) trong cơ sở dữ liệu quan hệ (RDBMS) cần một ID duy nhất để phân biệt, truy vấn và tránh trùng lặp.
    @PrimaryKey val id: Int,

    // Các cột (Columns) lưu thông tin tương ứng.
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,

    // Trạng thái theo dõi phim đã xem hay chưa.
    // Học thuật: Dữ liệu Boolean trong SQLite thường lưu dưới dạng Integer (0 hoặc 1). Room sẽ tự cast (chuyển đổi) ngầm định.
    val isWatched: Boolean = false,

    // Thời điểm người dùng thêm phim vào database (dùng để sắp xếp danh sách).
    // Timestamp lưu dưới dạng Long (milliseconds) rất chuẩn xác và tiện lợi.
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

// Mapper function: Đóng vai trò đóng gói Domain Model thành dạng Entity để lưu vào Database.
// Truyền thêm cờ (flag) `isWatched` để gán trạng thái cụ thể khi thao tác insert/update.
fun Movie.toEntity(isWatched: Boolean = false) = MovieEntity(
    id = id, title = title, overview = overview,
    posterPath = posterPath, backdropPath = backdropPath,
    voteAverage = voteAverage, releaseDate = releaseDate,
    isWatched = isWatched
)
