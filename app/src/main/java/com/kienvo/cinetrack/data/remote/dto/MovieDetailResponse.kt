package com.kienvo.cinetrack.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kienvo.cinetrack.domain.model.Movie

// MovieDetailResponse: Data class biểu diễn cấu trúc JSON trả về khi gọi API lấy chi tiết một bộ phim.
// Học thuật: Cũng là một DTO (Data Transfer Object). So với MovieListResponse (chỉ trả về list sơ sài), API chi tiết này trả thêm nhiều trường phức tạp hơn như runtime (thời lượng) hay genres (danh sách thể loại).
// Đời thường: Đây là "hồ sơ xin việc" đầy đủ chi tiết của một bộ phim, khác với "danh thiếp" ngắn gọn ở danh sách.
data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String,

    // Thời lượng phim (phút). Có thể null nếu hệ thống chưa cập nhật.
    val runtime: Int?,

    // Danh sách các thể loại phim (Ví dụ: Hành động, Hài).
    val genres: List<GenreDto>
) {
    // Hàm Mapper chuyển đổi về Domain Model.
    // Lưu ý: Hiện tại tầng Domain (Movie) chưa cần đến thời lượng hay thể loại nên mapper này đang được map giống hệ như phim ở dạng danh sách.
    fun toDomain() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        voteAverage = voteAverage, releaseDate = releaseDate
    )
}

// Cấu trúc lồng nhau (Nested object) mô tả riêng cho một Thể loại (Genre).
data class GenreDto(val id: Int, val name: String)