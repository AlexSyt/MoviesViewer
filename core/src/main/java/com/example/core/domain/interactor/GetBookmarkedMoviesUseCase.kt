package com.example.core.domain.interactor

import com.example.core.common.Result
import com.example.core.domain.model.Movie
import com.example.core.domain.repository.MoviesRepository

class GetBookmarkedMoviesUseCase(private val repository: MoviesRepository) {

    suspend operator fun invoke(): Result<List<Movie>> = repository.getBookmarkedMovies()
}