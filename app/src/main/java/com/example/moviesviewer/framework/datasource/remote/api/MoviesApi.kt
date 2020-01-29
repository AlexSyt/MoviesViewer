package com.example.moviesviewer.framework.datasource.remote.api

import com.example.moviesviewer.framework.datasource.remote.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("/discover/movie")
    suspend fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("primary_release_date.gte") releaseDateGte: String,
        @Query("primary_release_date.lte") releaseDateLte: String
    ): MoviesResponseDto
}