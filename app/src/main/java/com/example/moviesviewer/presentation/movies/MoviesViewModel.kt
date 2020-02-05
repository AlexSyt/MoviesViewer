package com.example.moviesviewer.presentation.movies

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Result
import com.example.core.common.Result.Loading
import com.example.core.common.Result.Success
import com.example.core.domain.interactor.BookmarkMovieUseCase
import com.example.core.domain.interactor.GetBookmarkedMoviesUseCase
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private typealias MoviesResult = Result<List<Movie>>

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getBookmarkedMoviesUseCase: GetBookmarkedMoviesUseCase,
    private val bookmarkMovieUseCase: BookmarkMovieUseCase
) : ViewModel() {

    private val _movies = MutableLiveData<Event<MoviesResult>>()
    val movies: LiveData<Event<MoviesResult>> = _movies

    private val _bookmarkedMovies = MutableLiveData<Event<MoviesResult>>()
    val bookmarkedMovies: LiveData<Event<MoviesResult>> = _bookmarkedMovies

    private val _shareMovieEvent = MutableLiveData<Event<String>>()
    val shareMovieEvent: LiveData<Event<String>> = _shareMovieEvent

    fun loadMovies(forceUpdate: Boolean = false) {
        val (dateGte, dateLte) = getDateFrame()
        fetchMovies(_movies) { getMoviesUseCase(dateGte, dateLte, forceUpdate) }
    }

    fun loadBookmarkedMovies() =
        fetchMovies(_bookmarkedMovies) { getBookmarkedMoviesUseCase() }

    fun onShareClicked(id: Int) {
        _shareMovieEvent.value = Event("$SHARE_BASE_URL/$id")
    }

    fun onBookmarkClicked(id: Int) {
        viewModelScope.launch {
            bookmarkMovieUseCase(id)
            loadMovies()
            loadBookmarkedMovies()
        }
    }

    private fun fetchMovies(
        receiver: MutableLiveData<Event<MoviesResult>>,
        action: suspend () -> MoviesResult
    ) {
        receiver.value = Event(Loading)
        viewModelScope.launch {
            val result = when (val moviesResult = action()) {
                is Success -> Success(moviesResult.data.sortedBy(Movie::title))
                else -> moviesResult
            }
            receiver.value = Event(result)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateFrame(): Pair<String, String> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.DAYS.toMillis(DAYS_PERIOD)
        val formatter = SimpleDateFormat(DATE_FORMAT)
        return Pair(formatter.format(Date(startTime)), formatter.format(Date(endTime)))
    }

    companion object {

        private const val SHARE_BASE_URL: String = "https://www.themoviedb.org/movie"
        private const val DAYS_PERIOD: Long = 14
        private const val DATE_FORMAT: String = "yyyy-MM-dd"
    }
}