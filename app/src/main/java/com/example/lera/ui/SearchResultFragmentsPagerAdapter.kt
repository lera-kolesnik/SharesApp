package com.example.lera.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SearchResultFragmentsPagerAdapter(
    activity: FragmentActivity,
    private val searchPresenter: SearchPresenter,
    private val watchListPresenter: WatchListPresenter,
    val itemsCount: Int
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SearchResultFragment(searchPresenter, watchListPresenter)
        fragment.arguments = Bundle().apply {
            val hasFavourite = position == 1
            putBoolean(HAS_FAVOURITE_ARG_TAG, hasFavourite)
        }
        return fragment
    }

    companion object {
        const val HAS_FAVOURITE_ARG_TAG = "object"
    }
}