package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Phát ra user hiện tại mỗi khi trạng thái đăng nhập thay đổi. */
    val currentUserFlow: Flow<User?>
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signOut()
}