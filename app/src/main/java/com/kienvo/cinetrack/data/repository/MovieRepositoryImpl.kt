package com.kienvo.cinetrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.kienvo.cinetrack.data.local.dao.MovieDao
import com.kienvo.cinetrack.data.local.entity.toDomain
import com.kienvo.cinetrack.data.local.entity.toEntity
import com.kienvo.cinetrack.data.remote.TmdbApiService
import com.kienvo.cinetrack.domain.model.CastMember
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.model.MovieDetail
import com.kienvo.cinetrack.domain.model.Video
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService,
    private val dao: MovieDao,
    private val firestoreRepo: FirestoreWatchlistRepository,
    private val auth: FirebaseAuth
) : MovieRepository {

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // API
    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> = runCatching {
        api.getPopularMovies(page).results.map { it.toDomain() }
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies(page).results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(id: Int): Result<MovieDetail> = runCatching {
        api.getMovieDetail(id).toDetailDomain()
    }

    override suspend fun searchMovies(query: String, page: Int): Result<List<Movie>> = runCatching {
        api.searchMovies(query, page).results.map { it.toDomain() }
    }

    override suspend fun getMovieCredits(id: Int): Result<List<CastMember>> = runCatching {
        api.getMovieCredits(id).cast.map { it.toDomain() }
    }

    override suspend fun getMovieVideos(id: Int): Result<List<Video>> = runCatching {
        api.getMovieVideos(id).results.map { it.toDomain() }
    }

    override suspend fun getRecommendations(id: Int): Result<List<Movie>> = runCatching {
        api.getRecommendations(id).results.map { it.toDomain() }
    }

    // Watchlist (Room + Firestore)
    override fun getWatchlist(): Flow<List<Movie>> {
        val uid = currentUserId ?: return emptyFlow()
        return dao.getAllWatchlist(uid).map { list -> list.map { it.toDomain() } }
    }

    override fun getWantToWatch(): Flow<List<Movie>> {
        val uid = currentUserId ?: return emptyFlow()
        return dao.getWantToWatch(uid).map { list -> list.map { it.toDomain() } }
    }

    override fun getWatched(): Flow<List<Movie>> {
        val uid = currentUserId ?: return emptyFlow()
        return dao.getWatched(uid).map { list -> list.map { it.toDomain() } }
    }

    override fun isInWatchlist(movieId: Int): Flow<Boolean> {
        val uid = currentUserId ?: return emptyFlow()
        return dao.isInWatchlist(uid, movieId)
    }

    override fun getWatchlistEntry(movieId: Int): Flow<Movie?> {
        val uid = currentUserId ?: return emptyFlow()
        return dao.getMovieEntry(uid, movieId).map { it?.toDomain() }
    }

    override suspend fun addToWatchlist(movie: Movie) {
        val uid = currentUserId ?: return
        dao.insertMovie(movie.toEntity(userId = uid))
        try {
            firestoreRepo.syncToFirestore(movie)
        } catch (e: Exception) {
            android.util.Log.w("MovieRepo", "Firestore sync failed", e)
        }
    }

    override suspend fun removeFromWatchlist(movie: Movie) {
        val uid = currentUserId ?: return
        dao.deleteMovie(uid, movie.id)
        try {
            firestoreRepo.removeFromFirestore(movie.id)
        } catch (e: Exception) {
            android.util.Log.w("MovieRepo", "Firestore remove failed", e)
        }
    }

    override suspend fun markAsWatched(movieId: Int, isWatched: Boolean) {
        val uid = currentUserId ?: return
        dao.updateWatchedStatus(uid, movieId, isWatched)
    }

    override suspend fun updateRatingAndNote(movieId: Int, rating: Int?, note: String?) {
        val uid = currentUserId ?: return
        dao.updateRatingAndNote(uid, movieId, rating, note)
        try {
            firestoreRepo.syncRatingAndNote(movieId, rating, note)
        } catch (e: Exception) {
            android.util.Log.w("MovieRepo", "Firestore rating sync failed", e)
        }
    }
}