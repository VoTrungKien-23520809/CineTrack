package com.kienvo.cinetrack.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kienvo.cinetrack.domain.model.CastMember

data class CreditsResponse(
    val cast: List<CastDto> = emptyList()
)

data class CastDto(
    val id: Int,
    val name: String,
    val character: String?,
    @SerializedName("profile_path") val profilePath: String?
) {
    fun toDomain() = CastMember(
        id = id,
        name = name,
        character = character.orEmpty(),
        profileUrl = profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
    )
}
