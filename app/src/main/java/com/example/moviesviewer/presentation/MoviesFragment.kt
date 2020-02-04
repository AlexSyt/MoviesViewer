package com.example.moviesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.core.common.Result
import com.example.core.common.Result.Error
import com.example.core.common.Result.Success
import com.example.core.domain.model.Movie
import com.example.moviesviewer.R
import com.example.moviesviewer.framework.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movies.*
import org.koin.android.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModel()

    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = MoviesAdapter(moviesViewModel::onShareClicked, moviesViewModel::onBookmarkClicked)
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { moviesViewModel.loadMovies(true) }

        moviesViewModel.apply {
            dataLoading.observe(viewLifecycleOwner, Observer(swipeRefreshLayout::setRefreshing))
            shareMovieEvent.observe(viewLifecycleOwner, EventObserver(::shareUrl))
            resultEvent.observe(viewLifecycleOwner, EventObserver(::handleResult))
            loadMovies()
        }
    }

    private fun handleResult(result: Result<List<Movie>>) {
        if (result is Success) {
            adapter.submitList(result.data)
        } else if (result is Error) {
            handleError(result.exception.message)
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