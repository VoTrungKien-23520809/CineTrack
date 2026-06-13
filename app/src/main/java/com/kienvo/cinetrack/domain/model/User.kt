package com.kienvo.cinetrack.domain.model

data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val providerId: String?
)
