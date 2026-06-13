package com.kienvo.cinetrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.kienvo.cinetrack.data.local.dao.MovieDao
import com.kienvo.cinetrack.data.local.entity.toDomain
import com.kienvo.cinetrack.data.local.entity.toEntity
import com.kienvo.cinetrack.data.remote.TmdbApiService
import com.kienvo.cinetrack.domain.model.Movie
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
    override suspend fun getPopularMovies(): Result<List<Movie>> = runCatching {
        api.getPopularMovies().results.map { it.toDomain() }
    }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> = runCatching {
        api.getTopRatedMovies().results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(id: Int): Result<Movie> = runCatching {
        api.getMovieDetail(id).toDomain()
    }

    override suspend fun searchMovies(query: String): Result<List<Movie>> = runCatching {
        api.searchMovies(query).results.map { it.toDomain() }
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

    override suspend fun addToWatchlist(movie: Movie) {
        val uid = currentUserId ?: return
        dao.insertMovie(movie.toEntity(userId = uid))
        try {
            firestoreRepo.syncToFirestore(movie)
        } catch (e: Exception) {
            // Firestore sync failed — local data is saved, cloud sync will be retried later
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
}