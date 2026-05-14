package com.kienvo.cinetrack.data.remote

import com.kienvo.cinetrack.data.remote.dto.MovieDetailResponse
import com.kienvo.cinetrack.data.remote.dto.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Interface (Giao diện) khai báo các đường dẫn API kết nối tới máy chủ (Server).
// Học thuật: Thư viện Retrofit sẽ dùng interface này để tự động sinh ra (generate) Code mạng (Networking code) lúc chạy. Lập trình viên không cần tự mở Connection, InputStream rườm rà.
// Đời thường: Đây là chiếc menu (thực đơn) đặt món ăn. Ghi rõ "món" bạn muốn (popular, top_rated), và "đầu bếp mạng" Retrofit sẽ tự biết cách nấu ra.
interface TmdbApiService {

    // Annotation @GET báo cho Retrofit biết đây là phương thức lấy dữ liệu (HTTP GET Method).
    // Phía trong là Endpoint ("địa chỉ ngách") đính kèm vào sau Base URL.
    @GET("movie/popular")
    // Từ khoá 'suspend' của Kotlin Coroutines.
    // Học thuật: Hàm bất đồng bộ (Asynchronous). Nó sẽ chạy ngầm không khoá luồng giao diện chính (Main Thread Blocking), khi nào có mạng trả về thì tiếp tục thực thi.
    suspend fun getPopularMovies(
        // @Query thêm tham số (parameters) lên thanh URL.
        // VD: url.com/movie/popular?api_key=XYZ&page=1
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    // Lõi đường dẫn có truyền ID linh động bên trong cặp {}.
    @GET("movie/{id}")
    suspend fun getMovieDetail(
        // @Path thay thế {id} trên URL bằng giá trị biến `id` được truyền vào lúc gọi hàm.
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): MovieDetailResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): MovieListResponse
}