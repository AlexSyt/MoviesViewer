package com.example.moviesviewer.presentation.movies

import androidx.lifecycle.LiveData
import com.example.core.common.Result
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.Event

class AllMoviesFragment : MoviesFragment() {

    override fun loadData(force: Boolean) =
        moviesViewModel.loadMovies(force)

    override fun getMoviesLiveData(): LiveData<Event<Result<List<Movie>>>> =
        moviesViewModel.movies
}