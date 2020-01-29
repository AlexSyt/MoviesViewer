package com.example.core.data

import com.example.core.common.Result
import com.example.core.domain.model.Movie

interface MoviesDataSource {

    suspend fun getMovies(): Result<List<Movie>>
}