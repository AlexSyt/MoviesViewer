package com.example.moviesviewer

import com.example.core.common.Result
import com.example.core.common.Result.Error
import com.example.core.common.Result.Success
import com.example.core.domain.model.Movie
import com.example.core.domain.repository.MoviesRepository
import com.example.core.domain.repository.MoviesRepositoryImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MoviesRepositoryImplTest {

    private val movie1 = Movie(1, "Title1", "Description1", null, false)
    private val movie2 = Movie(2, "Title2", "Description2", null, false)
    private val movie3 = Movie(3, "Title3", "Description3", null, false)

    private val newMovie = Movie(4, "Title4", "Description4", null, false)

    private val remoteMovies = listOf(movie1, movie2, movie3)
    private val localMovies = listOf<Movie>()
    private val newMovies = listOf(newMovie)

    private lateinit var moviesRemoteDataSource: FakeDataSource
    private lateinit var moviesLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var moviesRepository: MoviesRepositoryImpl

    @Before
    fun createRepository() {
        moviesRemoteDataSource = FakeDataSource(remoteMovies.toMutableList())
        moviesLocalDataSource = FakeDataSource(localMovies.toMutableList())
        moviesRepository = MoviesRepositoryImpl(
            moviesRemoteDataSource, moviesLocalDataSource, Dispatchers.Unconfined
        )
    }

    @Test
    fun getMovies_emptyRepositoryAndUninitializedCache() = runBlockingTest {
        val emptySource = FakeDataSource()
        val moviesRepository = MoviesRepositoryImpl(
            emptySource, emptySource, Dispatchers.Unconfined
        )

        assertThat(moviesRepository.getMovies()).isInstanceOf(Success::class.java)
    }

    @Test
    fun getMovies_repositoryCachesAfterFirstApiCall() = runBlockingTest {
        val initial = moviesRepository.getMovies()
        moviesRemoteDataSource.movies = newMovies.toMutableList()
        val second = moviesRepository.getMovies()

        // Initial and second should match because we didn't force a refresh
        assertThat(second).isEqualTo(initial)
    }

    @Test
    fun getMovies_requestsAllMoviesFromRemoteDataSource() = runBlockingTest {
        // When movies are requested from the repository
        val movies = moviesRepository.getMovies() as Success

        // Then movies are loaded from the remote data source
        assertThat(movies.data).isEqualTo(remoteMovies)
    }

    @Test
    fun getMovies_WithDirtyCache_moviesAreRetrievedFromRemote() = runBlockingTest {
        // First call returns from REMOTE
        val movies = moviesRepository.getMovies()

        // Set a different list of movies in REMOTE
        moviesRemoteDataSource.movies = newMovies.toMutableList()

        // But if movies are cached, subsequent calls load from cache
        val cachedMovies = moviesRepository.getMovies()
        assertThat(cachedMovies).isEqualTo(movies)

        // Now force remote loading
        val refreshedMovies = moviesRepository.getMovies(true) as Success

        // Movies must be the recently updated in REMOTE
        assertThat(refreshedMovies.data).isEqualTo(newMovies)
    }

    @Test
    fun getMovies_WithDirtyCache_remoteUnavailable_error() = runBlockingTest {
        // Make remote data source unavailable
        moviesRemoteDataSource.movies = null

        // Load movies forcing remote load
        val refreshedMovies = moviesRepository.getMovies(true)

        // Result should be an error
        assertThat(refreshedMovies).isInstanceOf(Error::class.java)
    }

    @Test
    fun getMovies_WithRemoteDataSourceUnavailable_MoviesAreRetrievedFromLocal() = runBlockingTest {
        // When the remote data source is unavailable
        moviesRemoteDataSource.movies = null

        // The repository fetches from the local source
        assertThat((moviesRepository.getMovies() as Success).data).isEqualTo(localMovies)
    }

    @Test
    fun getMovies_WithBothDataSourcesUnavailable_returnsError() = runBlockingTest {
        // When both sources are unavailable
        moviesRemoteDataSource.movies = null
        moviesLocalDataSource.movies = null

        // The repository returns an error
        assertThat(moviesRepository.getMovies()).isInstanceOf(Error::class.java)
    }

    @Test
    fun getMovies_refreshesLocalDataSource() = runBlockingTest {
        val initialLocal = moviesLocalDataSource.movies!!.toList()

        // First load will fetch from remote
        val newMovies = (moviesRepository.getMovies(true) as Success).data

        assertThat(newMovies).isEqualTo(remoteMovies)
        assertThat(newMovies).isEqualTo(moviesLocalDataSource.movies)
        assertThat(moviesLocalDataSource.movies).isNotEqualTo(initialLocal)
    }

    @Test
    fun bookmarkedMoviesAreSaved() = runBlockingTest {
        val movies = (moviesRepository.getMovies() as Success).data
        val bookmarkedId = movies.first().id
        moviesRepository.bookmarkMovie(bookmarkedId)

        // Load movies forcing remote load
        val refreshedMovies = (moviesRepository.getMovies(true) as Success).data

        // A previously bookmarked movie should remain bookmarked
        assertThat(refreshedMovies.find { it.id == bookmarkedId }?.bookmarked == true)
    }

    private suspend fun MoviesRepository.getMovies(
        forceUpdate: Boolean = false
    ): Result<List<Movie>> = getMovies("", "", forceUpdate)
}