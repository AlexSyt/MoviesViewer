package com.example.core.domain.interactor

import com.example.core.common.Result
import com.example.core.domain.model.Movie
import com.example.core.domain.repository.MoviesRepository

class GetMoviesUseCase(private val repository: MoviesRepository) {

    suspend operator fun invoke(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean = false
    ): Result<List<Movie>> = repository.getMovies(releaseDateGte, releaseDateLte, forceUpdate)
}