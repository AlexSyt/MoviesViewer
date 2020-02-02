package com.example.core.domain.repository

import com.example.core.common.Result
import com.example.core.common.Result.Success
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesDataSource,
    private val moviesLocalDataSource: MoviesDataSource
) : MoviesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val cachedMovies: MutableList<Movie> = ArrayList()

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean
    ): Result<List<Movie>> =
        withContext(ioDispatcher) {
            when {
                !forceUpdate && cachedMovies.isNotEmpty() -> Success(cachedMovies)
                else -> fetchMovies(releaseDateGte, releaseDateLte, forceUpdate)
                    .also { result ->
                        (result as? Success)?.let { refreshCache(it.data) }
                    }
            }
        }

    private suspend fun fetchMovies(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean
    ): Result<List<Movie>> {
        val remoteResult = moviesRemoteDataSource.getMovies(releaseDateGte, releaseDateLte)
        if (remoteResult is Success) {
            return remoteResult.also { refreshLocalDataSource(it.data) }
        } else if (forceUpdate) {
            return Result.Error(Exception("Can't force refresh: remote data source is unavailable"))
        }

        val localResult = moviesLocalDataSource.getMovies(releaseDateGte, releaseDateLte)
        return if (localResult is Success) {
            localResult
        } else {
            Result.Error(Exception("Can't refresh: all data sources is unavailable"))
        }
    }

    private suspend fun refreshLocalDataSource(movies: List<Movie>) {
        moviesLocalDataSource.removeAllMovies()
        movies.forEach { moviesLocalDataSource.saveMovie(it) }
    }

    private fun refreshCache(movies: List<Movie>) {
        cachedMovies.clear()
        cachedMovies.addAll(movies)
    }
}