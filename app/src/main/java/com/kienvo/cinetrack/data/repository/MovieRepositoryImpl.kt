package com.kienvo.cinetrack.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
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

    private val api = RetrofitInstance.api
    private val apiKey by lazy { ApiKeyProvider.getApiKey(context) }
    private val dao = CineTrackDatabase.getInstance(context).movieDao()
    private val firestoreRepo = FirestoreWatchlistRepository()

    // Lấy userId hiện tại — throw nếu chưa login
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("Chưa đăng nhập")

    // ── API ──────────────────────────────────────────
    override suspend fun getPopularMovies(): Result<List<Movie>> = runCatching {
        api.getPopularMovies(apiKey).results.map { it.toDomain() }
    }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies(apiKey).results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(id: Int): Result<Movie> = runCatching {
        api.getMovieDetail(id, apiKey).toDomain()
    }

    // ── Watchlist (Room + Firestore) ─────────────────
    override fun getWatchlist(): Flow<List<Movie>> =
        dao.getAllWatchlist(currentUserId).map { list -> list.map { it.toDomain() } }

    override fun getWantToWatch(): Flow<List<Movie>> =
        dao.getWantToWatch(currentUserId).map { list -> list.map { it.toDomain() } }

    override fun getWatched(): Flow<List<Movie>> =
        dao.getWatched(currentUserId).map { list -> list.map { it.toDomain() } }

    override fun isInWatchlist(movieId: Int): Flow<Boolean> =
        dao.isInWatchlist(currentUserId, movieId)

    override suspend fun addToWatchlist(movie: Movie) {
        dao.insertMovie(movie.toEntity(userId = currentUserId))
        firestoreRepo.syncToFirestore(movie)
    }

    override suspend fun removeFromWatchlist(movie: Movie) {
        // Cần tìm đúng entity theo userId + movieId để xóa
        dao.deleteMovie(movie.toEntity(userId = currentUserId))
        firestoreRepo.removeFromFirestore(movie.id)
    }

    override suspend fun markAsWatched(movieId: Int, isWatched: Boolean) =
        dao.updateWatchedStatus(currentUserId, movieId, isWatched)
}