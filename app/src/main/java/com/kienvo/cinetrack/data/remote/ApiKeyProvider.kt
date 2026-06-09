package com.kienvo.cinetrack.data.remote

import android.content.Context
import java.util.Properties

object ApiKeyProvider {
    private var apiKey: String? = null

    fun getApiKey(context: Context): String {
        if (apiKey != null) return apiKey!!

        return try {
            val properties = Properties()
            context.assets.open("secrets.properties").use { stream ->
                properties.load(stream)
            }
            apiKey = properties.getProperty("TMDB_API_KEY") ?: ""
            apiKey!!
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}