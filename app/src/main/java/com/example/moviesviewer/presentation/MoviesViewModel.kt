package com.example.moviesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Result
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.Event
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _items = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    val items: LiveData<List<Movie>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _loadingError = MutableLiveData<Event<String?>>()
    val loadingError: LiveData<Event<String?>> = _loadingError

    private val _shareMovieEvent = MutableLiveData<Event<String>>()
    val shareMovieEvent: LiveData<Event<String>> = _shareMovieEvent

    fun loadTasks(forceUpdate: Boolean = false) {
        _dataLoading.value = true
        viewModelScope.launch {
            val moviesResult = getMoviesUseCase("2019-09-15", "2019-10-22", forceUpdate)
            if (moviesResult is Result.Success) {
                _items.value = moviesResult.data
            } else if (moviesResult is Result.Error) {
                _loadingError.value = Event(moviesResult.exception.message)
            }
            _dataLoading.value = false
        }
    }

    fun onShareClicked(id: Int) {
        _shareMovieEvent.value = Event("$SHARE_BASE_URL/$id")
    }

    companion object {

        private const val SHARE_BASE_URL: String = "https://www.themoviedb.org/movie"
    }
}