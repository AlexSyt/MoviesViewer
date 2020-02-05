package com.example.moviesviewer.presentation.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moviesviewer.R
import com.example.moviesviewer.presentation.movies.AllMoviesFragment
import com.example.moviesviewer.presentation.movies.BookmarkedMoviesFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var pagerAdapter: MainPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = MainPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = context?.let { pagerAdapter.getTabTitle(it, position) }
        }.attach()
    }
}

class MainPagerAdapter(parent: Fragment) : FragmentStateAdapter(parent) {

    private val fragments: List<Pair<Int, Class<out Fragment>>> = listOf(
        R.string.all_movies to AllMoviesFragment::class.java,
        R.string.bookmarked_movies to BookmarkedMoviesFragment::class.java
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment =
        fragments[position].second.newInstance()

    fun getTabTitle(context: Context, position: Int): String =
        context.getString(fragments[position].first)
}