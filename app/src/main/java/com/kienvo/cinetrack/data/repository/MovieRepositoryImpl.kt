package com.kienvo.cinetrack.data.repository

import android.content.Context
import com.kienvo.cinetrack.data.local.CineTrackDatabase
import com.kienvo.cinetrack.data.local.entity.MovieEntity
import com.kienvo.cinetrack.data.local.entity.toDomain
import com.kienvo.cinetrack.data.local.entity.toEntity
import com.kienvo.cinetrack.data.remote.ApiKeyProvider
import com.kienvo.cinetrack.data.remote.RetrofitInstance
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repository Implementation (Cài đặt): Lớp "tổng quản" đứng ra điều phối việc kết nối Database (Room) và Mạng (Retrofit).
// Học thuật: Triển khai theo Repository Pattern. Nó giấu đi (abstract) việc lấy data từ nguồn nào (Local hay Remote), để ViewModel phía trên gọi là có xài ngay, không cần quan tâm chi tiết.
// Đời thường: Đây là gã "thủ kho/quản lý nhà kho". Khi ai đó ra lệnh "lấy danh sách phim cho tôi", gã sẽ biết phải chạy đi gọi Shipper (mạng) hay mở tủ (Database) để lấy đồ. Mọi người không cần tự làm mấy việc đấy.
class MovieRepositoryImpl(context: Context) : MovieRepository {

    // Lấy công cụ gọi mạng (API)
    private val api = RetrofitInstance.api

    // Lazy: Khởi tạo biến này lần đầu tiên được dùng (trì hoãn khởi tạo). Đây là cách giấu API Key qua helper.
    private val apiKey by lazy { ApiKeyProvider.getApiKey(context) }

    // Lấy công cụ thao tác Database (DAO)
    private val dao = CineTrackDatabase.getInstance(context).movieDao()
    private val firestoreRepo = FirestoreWatchlistRepository()

    // Lấy dữ liệu qua mạng với khối 'runCatching' (Tương tự try-catch).
    // Nếu block mã chạy bị lỗi (như rớt mạng, mất mạng), nó không sập app mà sẽ trả về một Result.failure() chứa Exception.
    override suspend fun getPopularMovies(): Result<List<Movie>> = runCatching {
        // Gắn DTO (mạng) map thành Domain (nội bộ app) trước khi trả về.
        api.getPopularMovies(apiKey).results.map { it.toDomain() }
    }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies(apiKey).results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(id: Int): Result<Movie> = runCatching {
        api.getMovieDetail(id, apiKey).toDomain()
    }


    // Các hàm tương tác với Local Database (Sử dụng Kotlin Flow)
    // Học thuật: Flow là Reactive Stream (Luồng dữ liệu phản ứng). Nó "mở một cái ống nước" nối từ Database lên bề mặt. Cứ Database thay đổi lập tức ống nước sẽ tự chảy data mới lên mà không cần gọi lại.
    override fun getWatchlist(): Flow<List<Movie>> =
        // Lấy danh sách kiểu MovieEntity từ DAO, biến đổi (map) dần thành danh sách Movie (Domain model)
        dao.getAllWatchlist().map { list -> list.map { it.toDomain() } }

    override fun getWantToWatch(): Flow<List<Movie>> =
        dao.getWantToWatch().map { list -> list.map { it.toDomain() } }

    override fun getWatched(): Flow<List<Movie>> =
        dao.getWatched().map { list -> list.map { it.toDomain() } }

    override fun isInWatchlist(movieId: Int): Flow<Boolean> =
        dao.isInWatchlist(movieId)

    // Các hàm Ghi / Xoá vào Database. Cần dùng suspend fun vì chúng thực thi I/O (Input/Output).
    override suspend fun addToWatchlist(movie: Movie) {
        dao.insertMovie(movie.toEntity())
        firestoreRepo.syncToFirestore(movie) // sync lên cloud
    }

    override suspend fun removeFromWatchlist(movie: Movie) {
        dao.deleteMovie(movie.toEntity())
        firestoreRepo.removeFromFirestore(movie.id) // xóa trên cloud
    }

    override suspend fun markAsWatched(movieId: Int, isWatched: Boolean) =
        dao.updateWatchedStatus(movieId, isWatched)
}