package com.kienvo.cinetrack.domain.model

data class Video(
    val key: String,
    val name: String,
    val site: String,
    val type: String
) {
    val isYoutube: Boolean get() = site.equals("YouTube", ignoreCase = true)
    val youtubeUrl: String get() = "https://www.youtube.com/watch?v=$key"
}
