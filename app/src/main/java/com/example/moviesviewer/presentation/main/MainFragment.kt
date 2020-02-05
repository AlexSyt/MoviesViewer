package com.example.moviesviewer.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moviesviewer.R
import com.example.moviesviewer.presentation.movies.MoviesFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var pagerAdapter: MainPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = MainPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pagerAdapter.getTabTitle(position)
        }.attach()
    }
}

class MainPagerAdapter(parent: Fragment) : FragmentStateAdapter(parent) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = MoviesFragment.newInstance()

    fun getTabTitle(position: Int): String = "Tab $position"
}