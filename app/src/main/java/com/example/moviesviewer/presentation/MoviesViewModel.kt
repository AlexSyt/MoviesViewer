package com.example.moviesviewer.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Result
import com.example.core.common.Result.Success
import com.example.core.domain.interactor.BookmarkMovieUseCase
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val bookmarkMovieUseCase: BookmarkMovieUseCase
) : ViewModel() {

    private val _resultEvent = MutableLiveData<Event<Result<List<Movie>>>>()
    val resultEvent: LiveData<Event<Result<List<Movie>>>> = _resultEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _shareMovieEvent = MutableLiveData<Event<String>>()
    val shareMovieEvent: LiveData<Event<String>> = _shareMovieEvent

    fun loadMovies(forceUpdate: Boolean = false) {
        _dataLoading.value = true
        viewModelScope.launch {
            val (dateGte, dateLte) = getDateFrame()
            val moviesResult = getMoviesUseCase(dateGte, dateLte, forceUpdate)
            if (moviesResult is Success) {
                _resultEvent.value = Event(Success(moviesResult.data.sortedBy(Movie::title)))
            } else if (moviesResult is Result.Error) {
                _resultEvent.value = Event(moviesResult)
            }
            _dataLoading.value = false
        }
    }

    fun onShareClicked(id: Int) {
        _shareMovieEvent.value = Event("$SHARE_BASE_URL/$id")
    }

    fun onBookmarkClicked(id: Int) {
        viewModelScope.launch {
            bookmarkMovieUseCase(id)
            loadMovies()
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