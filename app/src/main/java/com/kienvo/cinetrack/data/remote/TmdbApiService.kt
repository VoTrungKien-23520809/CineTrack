package com.kienvo.cinetrack.data.remote

import com.kienvo.cinetrack.data.remote.dto.MovieDetailResponse
import com.kienvo.cinetrack.data.remote.dto.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): MovieDetailResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): MovieListResponse
}