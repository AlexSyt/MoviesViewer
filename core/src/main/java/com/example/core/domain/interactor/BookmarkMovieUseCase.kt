package com.example.core.domain.interactor

import com.example.core.domain.repository.MoviesRepository

class BookmarkMovieUseCase(private val repository: MoviesRepository) {

    suspend operator fun invoke(id: Int) = repository.bookmarkMovie(id)
}