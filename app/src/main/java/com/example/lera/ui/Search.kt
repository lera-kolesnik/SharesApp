package com.example.lera.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.lera.R
import com.example.lera.app.App
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Holds logic to display Search view with list of stocks
 */
class Search : FragmentActivity(), CoroutineScope {

    lateinit var searchEt: EditText
    lateinit var searchPresenter: SearchPresenter
    lateinit var watchListPresenter: WatchListPresenter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchPresenter = (applicationContext as App).component.searchPresenter()
        watchListPresenter = (applicationContext as App).component.watchListPresenter()
        initUi()
    }

    private fun initUi(){
        viewPager = findViewById(R.id.pager)
        val pagerAdapter = SearchResultFragmentsPagerAdapter(
            this,
            searchPresenter,
            watchListPresenter,
            itemsCount = 2
        )
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Stocks"
                1 -> tab.text = "Favourite"
            }
        }.attach()

        val watcher = object : TextWatcher {
            private var searchFor = ""
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor) return
                searchFor = searchText
                launch {
                    delay(1000)
                    if (searchText != searchFor) return@launch
                    searchPresenter.search(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) =  Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        }
        searchEt = findViewById(R.id.searchEt)
        searchEt.addTextChangedListener(watcher)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}
