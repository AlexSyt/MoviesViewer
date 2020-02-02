package com.example.core.data

import com.example.core.common.Result
import com.example.core.domain.model.Movie

interface MoviesDataSource {

    suspend fun getMovies(releaseDateGte: String, releaseDateLte: String): Result<List<Movie>>

    suspend fun saveMovie(movie: Movie)

    suspend fun removeAllMovies()
}