package com.example.moviesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.moviesviewer.R
import kotlinx.android.synthetic.main.fragment_movies.*
import org.koin.android.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModel()

    private val adapter: MoviesAdapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { moviesViewModel.loadTasks(true) }

        moviesViewModel.apply {
            items.observe(viewLifecycleOwner, Observer(adapter::submitList))
            dataLoading.observe(viewLifecycleOwner, Observer(swipeRefreshLayout::setRefreshing))
            loadTasks()
        }
    }
}