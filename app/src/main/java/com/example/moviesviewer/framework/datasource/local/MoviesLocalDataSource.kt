package com.example.moviesviewer.framework.datasource.local

import com.example.core.common.Mapper
import com.example.core.common.Result
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.datasource.local.db.MoviesDao
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto
import com.example.moviesviewer.framework.datasource.local.mapper.CachedMovieToMovieMapper
import com.example.moviesviewer.framework.datasource.local.mapper.MovieToCachedMovieMapper

class MoviesLocalDataSource(
    private val moviesDao: MoviesDao
) : MoviesDataSource {

    private val cachedMovieToMovie: Mapper<MovieCachedDto, Movie> = CachedMovieToMovieMapper()
    private val movieToCachedMovie: Mapper<Movie, MovieCachedDto> = MovieToCachedMovieMapper()

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String
    ): Result<List<Movie>> = fetchMovies { moviesDao.getMovies() }

    override suspend fun getBookmarkedMovies(): Result<List<Movie>> =
        fetchMovies { moviesDao.getBookmarkedMovies() }

    override suspend fun getBookmarkedIds(): Set<Int> = moviesDao.getBookmarkedIds().toSet()

    override suspend fun saveMovie(movie: Movie) =
        moviesDao.insertMovie(movieToCachedMovie.map(movie))

    override suspend fun updateMovie(movie: Movie) =
        moviesDao.updateMovie(movieToCachedMovie.map(movie))

    override suspend fun removeAllMovies() = moviesDao.deleteMovies()

    private suspend fun fetchMovies(
        action: suspend () -> List<MovieCachedDto>
    ): Result<List<Movie>> =
        try {
            Result.Success(action().map(cachedMovieToMovie::map))
        } catch (exception: Exception) {
            Result.Error(exception)
        }
}