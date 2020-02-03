package com.example.core.domain.repository

import com.example.core.common.Result
import com.example.core.common.Result.Success
import com.example.core.data.MoviesDataSource
import com.example.core.domain.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesDataSource,
    private val moviesLocalDataSource: MoviesDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoviesRepository {

    private val cachedMovies: ConcurrentMap<Int, Movie> = ConcurrentHashMap()

    override suspend fun getMovies(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean
    ): Result<List<Movie>> =
        withContext(dispatcher) {
            when {
                !forceUpdate && cachedMovies.isNotEmpty() -> Success(cachedMovies.values.toList())
                else -> fetchMovies(releaseDateGte, releaseDateLte, forceUpdate)
                    .also { result ->
                        (result as? Success)?.let { refreshCache(it.data) }
                    }
            }
        }

    override suspend fun bookmarkMovie(id: Int) {
        cachedMovies[id]?.let { movie ->
            val updated = movie.copy(bookmarked = !movie.bookmarked)
            cachedMovies[id] = updated
            moviesLocalDataSource.updateMovie(updated)
        }
    }

    private suspend fun fetchMovies(
        releaseDateGte: String,
        releaseDateLte: String,
        forceUpdate: Boolean
    ): Result<List<Movie>> {
        val remoteResult = moviesRemoteDataSource.getMovies(releaseDateGte, releaseDateLte)
        if (remoteResult is Success) {
            val movies = updateBookmarks(remoteResult.data)
            refreshLocalDataSource(movies)
            return Success(movies)
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

    private suspend fun updateBookmarks(remoteMovies: List<Movie>): List<Movie> {
        val bookmarkedIds = moviesLocalDataSource.getBookmarkedIds()
        if (bookmarkedIds.isNotEmpty()) {
            return remoteMovies.map {
                if (bookmarkedIds.contains(it.id)) {
                    it.copy(bookmarked = true)
                } else {
                    it
                }
            }
        }
        return remoteMovies
    }

    private suspend fun refreshLocalDataSource(movies: List<Movie>) {
        moviesLocalDataSource.removeAllMovies()
        movies.forEach { moviesLocalDataSource.saveMovie(it) }
    }

    private fun refreshCache(movies: List<Movie>) {
        cachedMovies.clear()
        movies.forEach { cachedMovies[it.id] = it }
    }
}