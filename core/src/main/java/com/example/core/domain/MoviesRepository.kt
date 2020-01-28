package com.example.core.domain

import com.example.core.common.Result

interface MoviesRepository {

    suspend fun getMovies(forceUpdate: Boolean = false): Result<List<Movie>>
}