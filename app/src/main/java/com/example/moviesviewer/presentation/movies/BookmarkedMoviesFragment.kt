package com.example.moviesviewer.presentation.movies

import androidx.lifecycle.LiveData
import com.example.core.common.Result
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.Event

class BookmarkedMoviesFragment : MoviesFragment() {

    override fun loadData(force: Boolean) =
        moviesViewModel.loadBookmarkedMovies()

    override fun getMoviesLiveData(): LiveData<Event<Result<List<Movie>>>> =
        moviesViewModel.bookmarkedMovies
}