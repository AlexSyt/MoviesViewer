package com.example.moviesviewer

import com.example.core.common.Result
import com.example.core.common.Result.Error
import com.example.core.common.Result.Success
import com.example.core.domain.model.Movie
import com.example.core.domain.repository.MoviesRepository

class FakeRepository : MoviesRepository {

    var movies: MutableMap<Int, Movie> = LinkedHashMap()

    var shouldReturnError: Boolean = false

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean
    ): Result<List<Movie>> = valueOrError { movies.values.toList() }

    override suspend fun getBookmarkedMovies(): Result<List<Movie>> =
        valueOrError { movies.values.filter(Movie::bookmarked) }

    override suspend fun bookmarkMovie(id: Int) {
        movies[id]?.let { movie ->
            val bookmarked = movie.copy(bookmarked = !movie.bookmarked)
            movies[id] = bookmarked
        }
    }

    private fun valueOrError(action: () -> List<Movie>): Result<List<Movie>> =
        if (shouldReturnError) Error(Exception("Test exception")) else Success(action())
}