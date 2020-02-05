package com.example.moviesviewer

import com.example.core.common.Result.Error
import com.example.core.common.Result.Success
import com.example.core.domain.interactor.BookmarkMovieUseCase
import com.example.core.domain.interactor.GetBookmarkedMoviesUseCase
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.model.Movie
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class UseCasesTest {

    private val repository = FakeRepository()

    private val getMoviesUseCase = GetMoviesUseCase(repository)
    private val getBookmarkedMoviesUseCase = GetBookmarkedMoviesUseCase(repository)
    private val bookmarkMovieUseCase = BookmarkMovieUseCase(repository)

    @Test
    fun getMovies_empty() = runBlockingTest {
        // Given an empty repository

        // When calling the use case
        val result = getMoviesUseCase("", "")

        // Verify the result is a success and empty
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).data).isEmpty()
    }

    @Test
    fun getBookmarkedMovies_empty() = runBlockingTest {
        // Given an empty repository

        // When calling the use case
        val result = getBookmarkedMoviesUseCase()

        // Verify the result is a success and empty
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).data).isEmpty()
    }

    @Test
    fun getMovies_error() = runBlockingTest {
        // Make the repository return errors
        repository.shouldReturnError = true

        // Load movies
        val result = getMoviesUseCase("", "")

        // Verify the result is an error
        assertThat(result).isInstanceOf(Error::class.java)
    }

    @Test
    fun getBookmarkedMovies_error() = runBlockingTest {
        // Make the repository return errors
        repository.shouldReturnError = true

        // Load movies
        val result = getBookmarkedMoviesUseCase()

        // Verify the result is an error
        assertThat(result).isInstanceOf(Error::class.java)
    }

    @Test
    fun getMovies_returnsAllMovies() = runBlockingTest {
        // Given a repository with 2 bookmarked and 2 unbookmarked movies
        fillMovies()

        // Load movies
        val result = getMoviesUseCase("", "")

        // Verify the result is a success and correct
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).data).isEqualTo(repository.movies.values.toList())
    }

    @Test
    fun getBookmarkedMovies_returnsBookmarkedMovies() = runBlockingTest {
        // Given a repository with 2 bookmarked and 2 unbookmarked movies
        fillMovies()

        // Load movies
        val result = getBookmarkedMoviesUseCase()

        // Verify the result is a success and correct
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).data).isEqualTo(repository.movies.values.filter(Movie::bookmarked))
    }

    @Test
    fun bookmarkMovie_bookmarksMovie() = runBlockingTest {
        // Given a repository with 2 unbookmarked movies
        repository.movies.apply {
            put(1, Movie(1, "Title1", "Description1", null, false))
            put(2, Movie(2, "Title2", "Description2", null, false))
        }

        // Bookmark movies
        bookmarkMovieUseCase(1)
        bookmarkMovieUseCase(2)

        // Load movies
        val movies = (repository.getBookmarkedMovies() as Success).data

        // Verify the movies are bookmarked
        assertThat(movies).hasSize(2)
        assertTrue(movies.all(Movie::bookmarked))
    }

    private fun fillMovies() =
        repository.movies.apply {
            put(1, Movie(1, "Title1", "Description1", null, false))
            put(2, Movie(2, "Title2", "Description2", null, false))
            put(3, Movie(3, "Title3", "Description3", null, true))
            put(4, Movie(4, "Title4", "Description4", null, true))
        }
}