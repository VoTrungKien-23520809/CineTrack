package com.kienvo.cinetrack.data.remote

import com.kienvo.cinetrack.data.remote.dto.CreditsResponse
import com.kienvo.cinetrack.data.remote.dto.MovieDetailResponse
import com.kienvo.cinetrack.data.remote.dto.MovieListResponse
import com.kienvo.cinetrack.data.remote.dto.VideosResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int
    ): MovieDetailResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/{id}/credits")
    suspend fun getMovieCredits(
        @Path("id") id: Int
    ): CreditsResponse

    @GET("movie/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") id: Int
    ): VideosResponse

    @GET("movie/{id}/recommendations")
    suspend fun getRecommendations(
        @Path("id") id: Int,
        @Query("page") page: Int = 1
    ): MovieListResponse
}