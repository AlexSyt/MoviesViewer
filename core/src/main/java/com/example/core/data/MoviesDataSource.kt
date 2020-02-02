package com.example.core.data

import com.example.core.common.Result
import com.example.core.domain.model.Movie

interface MoviesDataSource {

    suspend fun getMovies(releaseDateGte: String, releaseDateLte: String): Result<List<Movie>>

    suspend fun getBookmarkedIds(): Set<Int>

    suspend fun saveMovie(movie: Movie)

    suspend fun updateMovie(movie: Movie)

    suspend fun removeAllMovies()
}