package com.kienvo.cinetrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.kienvo.cinetrack.domain.model.User
import com.kienvo.cinetrack.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUserFlow: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun getCurrentUser(): User? = auth.currentUser?.toDomain()
    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val user = auth.signInWithCredential(credential).await().user
            ?: throw Exception("Đăng nhập thất bại")
        user.toDomain()
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<User> = runCatching {
        val user = auth.signInWithEmailAndPassword(email, password).await().user
            ?: throw Exception("Đăng nhập thất bại")
        user.toDomain()
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Đăng ký thất bại")

        // Cập nhật display name sau khi tạo tài khoản
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdate).await()
        user.toDomain()
    }

    override suspend fun signOut() = auth.signOut()
}

private fun FirebaseUser.toDomain() = User(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl?.toString(),
    providerId = providerData.firstOrNull { it.providerId != "firebase" }?.providerId
)