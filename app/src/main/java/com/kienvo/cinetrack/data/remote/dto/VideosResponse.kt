package com.kienvo.cinetrack.data.remote.dto

import com.kienvo.cinetrack.domain.model.Video

data class VideosResponse(
    val results: List<VideoDto> = emptyList()
)

data class VideoDto(
    val key: String,
    val name: String,
    val site: String,
    val type: String
) {
    fun toDomain() = Video(key = key, name = name, site = site, type = type)
}
