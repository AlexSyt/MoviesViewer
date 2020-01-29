package com.example.core.domain.repository

import com.example.core.common.Result
import com.example.core.common.Result.Success
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesDataSource
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
                else -> fetchMovies(releaseDateGte, releaseDateLte).also { result ->
                    (result as? Success)?.let { refreshCache(it.data) }
                }
            }
        }

    private suspend fun fetchMovies(
        releaseDateGte: String,
        releaseDateLte: String
    ): Result<List<Movie>> = moviesRemoteDataSource.getMovies(releaseDateGte, releaseDateLte)

    private fun refreshCache(movies: List<Movie>) {
        cachedMovies.clear()
        cachedMovies.addAll(movies)
    }
}