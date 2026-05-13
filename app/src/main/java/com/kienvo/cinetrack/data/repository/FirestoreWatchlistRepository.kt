package com.kienvo.cinetrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kienvo.cinetrack.domain.model.Movie
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreWatchlistRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun watchlistRef() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("watchlist")
    }

    // Sync lên Firestore khi thêm vào watchlist
    suspend fun syncToFirestore(movie: Movie, isWatched: Boolean = false) {
        watchlistRef()?.document(movie.id.toString())?.set(
            mapOf(
                "id" to movie.id,
                "title" to movie.title,
                "posterPath" to movie.posterPath,
                "voteAverage" to movie.voteAverage,
                "releaseDate" to movie.releaseDate,
                "isWatched" to isWatched,
                "addedAt" to System.currentTimeMillis()
            )
        )?.await()
    }

    // Xóa khỏi Firestore
    suspend fun removeFromFirestore(movieId: Int) {
        watchlistRef()?.document(movieId.toString())?.delete()?.await()
    }

    // Observe watchlist realtime từ Firestore
    fun observeWatchlist(): Flow<List<Map<String, Any>>> = callbackFlow {
        val ref = watchlistRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
            trySend(list)
        }

        awaitClose { listener.remove() }
    }
}