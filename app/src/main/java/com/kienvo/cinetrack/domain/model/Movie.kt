package com.kienvo.cinetrack.domain.model

// Domain Model: Data class đại diện cho thực thể (Entity) cốt lõi của ứng dụng.
// Học thuật: Trong Clean Architecture, lớp Domain (Lõi) không được phụ thuộc vào bất kỳ framework hay thư viện bên ngoài nào (như Room, Retrofit, Gson, Compose).
// Đời thường: Đây là khuôn mẫu "thuần khiết" nhất quy định một "Bộ phim" trong app của bạn sẽ có những thông tin gì. Nó không quan tâm dữ liệu này từ đâu ra (mạng hay database).
data class Movie(
    // Mã định danh duy nhất của bộ phim.
    val id: Int,

    // Tiêu đề phim.
    val title: String,

    // Tóm tắt nội dung phim.
    val overview: String,

    // Đường dẫn tương đối của ảnh bìa (có thể null nếu phim không có poster).
    val posterPath: String?,

    // Đường dẫn tương đối của ảnh phông nền (dùng cho list và chi tiết).
    val backdropPath: String?,

    // Điểm số đánh giá trung bình.
    val voteAverage: Double,

    // Ngày phát hành dưới dạng chuỗi.
    val releaseDate: String
) {
    // Helper function (Hàm tiện ích) - Đóng gói logic (Encapsulation trong OOP).
    // Học thuật: Tuân thủ nguyên tắc DRY (Don't Repeat Yourself). Bất cứ UI nào cần hiển thị ảnh chỉ việc gọi hàm này để lấy URL hoàn chỉnh, thay vì tự nối chuỗi thủ công.
    // Đời thường: Giống như bạn chỉ lưu "Số 5 ngõ 10", hàm này sẽ tự động gắn thêm "Thành phố Hà Nội" vào để ra địa chỉ đầy đủ dễ tìm trên mạng.
    fun fullPosterUrl() = "https://image.tmdb.org/t/p/w500$posterPath"

    // Nối chuỗi để lấy URL của ảnh backdrop với quality w780.
    fun fullBackdropUrl() = "https://image.tmdb.org/t/p/w780$backdropPath"

    // Định dạng điểm số, đảm bảo chỉ có 1 chữ số đằng sau dấu phẩy (vd: 8.46 -> 8.5)
    fun formattedRating() = String.format("%.1f", voteAverage)
}