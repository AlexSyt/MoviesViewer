package com.example.moviesviewer.presentation.movies

import android.os.Bundle
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.core.common.Result
import com.example.core.common.Result.*
import com.example.core.domain.model.Movie
import com.example.moviesviewer.R
import com.example.moviesviewer.framework.Event
import com.example.moviesviewer.framework.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class MoviesFragment : Fragment(R.layout.fragment_movies) {

    protected val moviesViewModel: MoviesViewModel by sharedViewModel()

    private lateinit var adapter: MoviesAdapter

    protected abstract fun loadData(force: Boolean = false)

    protected abstract fun getMoviesLiveData(): LiveData<Event<Result<List<Movie>>>>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = MoviesAdapter(moviesViewModel::onShareClicked, moviesViewModel::onBookmarkClicked)
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { loadData(true) }

        moviesViewModel.shareMovieEvent.observe(viewLifecycleOwner, EventObserver(::shareUrl))
        getMoviesLiveData().observe(viewLifecycleOwner, EventObserver(::handleResult))
        loadData()
    }

    private fun handleResult(result: Result<List<Movie>>) {
        swipeRefreshLayout.isRefreshing = result is Loading
        when (result) {
            is Success -> adapter.submitList(result.data)
            is Error -> handleError(result.exception.message)
        }
    }

    private fun shareUrl(url: String) {
        val intent = ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setChooserTitle(R.string.share_movie_link)
            .setText(url)
            .createChooserIntent()
        context?.packageManager?.let { pm ->
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent)
            }
        }
    }

    private fun handleError(message: String?) {
        if (message != null) {
            view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
        }
    }
}