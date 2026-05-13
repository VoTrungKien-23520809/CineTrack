package com.kienvo.cinetrack.data.remote.dto

// Import thư viện Gson của Google dùng để "parse" (phân tích) chuỗi JSON thành Object trong Kotlin.
// Học thuật: Gson hỗ trợ Deserialize/Serialize (Giải mã/Mã hoá) dữ liệu giữa định dạng trao đổi (JSON) và cấu trúc bộ nhớ (Data Class).
import com.google.gson.annotations.SerializedName
import com.kienvo.cinetrack.domain.model.Movie

// Data class đại diện cho toàn bộ cục dữ liệu (Response) mà server API (TMDB) trả về khi lấy danh sách phim.
// DTO (Data Transfer Object) pattern: Object chuyên dùng để vận chuyển dữ liệu qua lại giữa các hệ thống mạng (Client - Server).
// Đời thường: Đây như cái thùng lớn bọc tất cả dữ liệu từ shipper mạng giao đến.
data class MovieListResponse(
    // Số trang hiện tại của danh sách (phân trang - Pagination).
    val page: Int,

    // Danh sách các bộ phim nằm trong trang này.
    val results: List<MovieDto>,

    // @SerializedName dùng để ánh xạ (map) chính xác tên trường "total_pages" từ JSON gốc sang tên biến "totalPages" theo chuẩn naming convention của Kotlin (camelCase).
    @SerializedName("total_pages") val totalPages: Int
)

// Data class đại diện cho 1 bộ phim do API trả về.
// Đời thường: Đây là từng món hàng cụ thể nằm trong "thùng lớn" phía trên, chứa dữ liệu thô chưa qua chế biến.
data class MovieDto(
    // Định danh duy nhất (Primary Identifier) của bộ phim trên hệ thống database của Server.
    val id: Int,

    // Tiêu đề của bộ phim.
    val title: String,

    // Đoạn văn ngắn tóm tắt nội dung (synopsis) phim.
    val overview: String,

    // Đường dẫn ảnh bìa (poster) của phim. Định dạng nullable String ("?") vì không phải lúc nào backend cũng trả về ảnh.
    @SerializedName("poster_path") val posterPath: String?,

    // Đường dẫn ảnh nền ngang (backdrop) của phim.
    @SerializedName("backdrop_path") val backdropPath: String?,

    // Điểm đánh giá trung bình. Dùng kiểu dữ liệu Double để đại diện cho số thập phân (thực số), ví dụ: 8.5.
    @SerializedName("vote_average") val voteAverage: Double,

    // Ngày phát hành dưới dạng chuỗi (thường là chuẩn ISO "YYYY-MM-DD").
    @SerializedName("release_date") val releaseDate: String
) {
    // Mapper function: Hàm chuyển đổi từ DTO (dành xử lý Network) sang Domain Model (dành xử lý nội bộ App).
    // Học thuật: Áp dụng nguyên lý Separation of Concerns (Phân tách mối quan tâm) trong Clean Architecture.
    // Việc này giúp cách ly logic cốt lõi của ứng dụng khỏi sự thay đổi bất chợt từ cấu trúc API (Backend team đổi API thì UI/Business logic không bị ảnh).
    fun toDomain() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        voteAverage = voteAverage, releaseDate = releaseDate
    )
}