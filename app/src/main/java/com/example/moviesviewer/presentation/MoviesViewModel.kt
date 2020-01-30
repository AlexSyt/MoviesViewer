package com.example.moviesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Result
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.model.Movie
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _items = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    val items: LiveData<List<Movie>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _loadingError = MutableLiveData<String?>()
    val loadingError: LiveData<String?> = _loadingError

    fun loadTasks(forceUpdate: Boolean = false) {
        _dataLoading.value = true
        viewModelScope.launch {
            val moviesResult = getMoviesUseCase("2019-09-15", "2019-10-22", forceUpdate)
            if (moviesResult is Result.Success) {
                _loadingError.value = null
                _items.value = moviesResult.data
            } else if (moviesResult is Result.Error) {
                _loadingError.value = moviesResult.exception.message
            }
            _dataLoading.value = false
        }
    }
}