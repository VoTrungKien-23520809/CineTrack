package com.kienvo.cinetrack.domain.repository

import com.kienvo.cinetrack.domain.model.User

interface AuthRepository {
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signOut()
}