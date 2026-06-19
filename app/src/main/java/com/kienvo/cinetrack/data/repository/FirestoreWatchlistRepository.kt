package com.kienvo.cinetrack.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kienvo.cinetrack.domain.model.Movie
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreWatchlistRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun watchlistRef() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("watchlist")
    }

    // Sync lên Firestore khi thêm vào watchlist
    suspend fun syncToFirestore(movie: Movie, isWatched: Boolean = false) {
        val ref = watchlistRef()
        if (ref == null) {
            Log.w("FirestoreWatchlist", "Cannot sync: user not logged in")
            return
        }
        ref.document(movie.id.toString()).set(
            mapOf(
                "id" to movie.id,
                "title" to movie.title,
                "posterPath" to movie.posterPath,
                "voteAverage" to movie.voteAverage,
                "releaseDate" to movie.releaseDate,
                "isWatched" to isWatched,
                "addedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun syncRatingAndNote(movieId: Int, rating: Int?, note: String?) {
        val ref = watchlistRef() ?: return
        ref.document(movieId.toString()).set(
            mapOf("userRating" to rating, "note" to note),
            SetOptions.merge()
        ).await()
    }

    // Xóa khỏi Firestore
    suspend fun removeFromFirestore(movieId: Int) {
        val ref = watchlistRef()
        if (ref == null) {
            Log.w("FirestoreWatchlist", "Cannot remove: user not logged in")
            return
        }
        ref.document(movieId.toString()).delete().await()
    }
}