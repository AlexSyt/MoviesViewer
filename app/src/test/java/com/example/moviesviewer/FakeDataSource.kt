package com.example.moviesviewer

import com.example.core.common.Result
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie

class FakeDataSource(var movies: MutableList<Movie>? = mutableListOf()) : MoviesDataSource {

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String
    ): Result<List<Movie>> {
        movies?.let { return Result.Success(it) }
        return Result.Error(Exception("Movies not found"))
    }

    override suspend fun getBookmarkedMovies(): Result<List<Movie>> {
        movies?.let { return Result.Success(it.filter(Movie::bookmarked)) }
        return Result.Error(Exception("Movies not found"))
    }

    override suspend fun getBookmarkedIds(): Set<Int> =
        movies?.asSequence()
            ?.filter(Movie::bookmarked)
            ?.map(Movie::id)
            ?.toSet()
            ?: emptySet()

    override suspend fun saveMovie(movie: Movie) {
        movies?.add(movie)
    }

    override suspend fun updateMovie(movie: Movie) {
        val deleted = movies?.removeIf { it.id == movie.id }
        if (deleted == true) movies?.add(movie)
    }

    override suspend fun removeAllMovies() {
        movies?.clear()
    }
}