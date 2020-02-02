package com.example.moviesviewer.framework.datasource.remote

import com.example.core.common.Mapper
import com.example.core.common.Result
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.datasource.remote.api.MoviesApi
import com.example.moviesviewer.framework.datasource.remote.dto.MovieDto
import com.example.moviesviewer.framework.datasource.remote.mapper.MovieDtoToMovieMapper

class MoviesRemoteDataSource(
    private val api: MoviesApi,
    private val apiKey: String
) : MoviesDataSource {

    private val mapper: Mapper<MovieDto, Movie> = MovieDtoToMovieMapper()

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String
    ): Result<List<Movie>> =
        try {
            val moviesResponse = api.getMovies(apiKey, releaseDateGte, releaseDateLte)
            Result.Success(moviesResponse.movies.map(mapper::map))
        } catch (exception: Exception) {
            Result.Error(exception)
        }

    override suspend fun saveMovie(movie: Movie) = Unit

    override suspend fun updateMovie(movie: Movie) = Unit

    override suspend fun removeAllMovies() = Unit
}