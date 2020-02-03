package com.example.moviesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
            items.observe(viewLifecycleOwner, Observer(adapter::submitList))
            dataLoading.observe(viewLifecycleOwner, Observer(swipeRefreshLayout::setRefreshing))
            shareMovieEvent.observe(viewLifecycleOwner, EventObserver(::shareUrl))
            loadingError.observe(viewLifecycleOwner, EventObserver(::handleError))
            loadMovies()
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