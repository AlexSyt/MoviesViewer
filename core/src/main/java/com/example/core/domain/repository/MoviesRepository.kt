package com.example.core.domain.repository

import com.example.core.common.Result
import com.example.core.domain.model.Movie

interface MoviesRepository {

    suspend fun getMovies(
        forceUpdate: Boolean = false,
        releaseDateGte: String,
        releaseDateLte: String
    ): Result<List<Movie>>
}