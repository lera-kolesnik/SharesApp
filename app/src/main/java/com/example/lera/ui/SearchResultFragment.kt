package com.example.lera.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lera.R
import com.example.lera.adapter.CompanyListAdapter
import com.example.lera.data.model.Company
import com.example.lera.data.model.WatchListItem
import com.example.lera.ui.SearchResultFragmentsPagerAdapter.Companion.HAS_FAVOURITE_ARG_TAG
import com.example.lera.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class SearchResultFragment(
    private val searchPresenter: SearchPresenter,
    private val watchListPresenter: WatchListPresenter
) : Fragment(), SearchView, WatchListView, CoroutineScope {

    private lateinit var viewAnimator: DefaultItemAnimator
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: CompanyListAdapter
    private lateinit var companyListRv: RecyclerView
    private var shouldShowFavouriteStocksOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey(HAS_FAVOURITE_ARG_TAG) }?.apply {
            shouldShowFavouriteStocksOnly = getBoolean(HAS_FAVOURITE_ARG_TAG)
        }

        if (shouldShowFavouriteStocksOnly) {
            watchListPresenter.attach(this)
            watchListPresenter.getWatchListItems()
        } else {
            searchPresenter.attach(this)
            searchPresenter.loadCompanies()
        }

        viewAdapter = CompanyListAdapter(
            requireContext(),
            watchListPresenter,
            object: ClickListener {
                override fun onPositionClicked(position: Int) {
                    val company = viewAdapter.get(position)
                    val intent = Intent(activity, ViewStock::class.java)
                    intent.putExtra(Constants.EXTRA_STOCK_NAME, company.name)
                    intent.putExtra(Constants.EXTRA_STOCK_SYMBOL, company.symbol)
                    startActivity(intent)
                }
            }
        )
        viewManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL ,false)
        viewAnimator = DefaultItemAnimator()
    }

    interface ClickListener {
        fun onPositionClicked(position: Int)
    }

    override fun onDestroy() {
        if (shouldShowFavouriteStocksOnly) {
            watchListPresenter.drop(this)
        } else {
            searchPresenter.drop(this)
        }
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view  = inflater.inflate(R.layout.search_fragment_layout, container, false)
        companyListRv = view.findViewById(R.id.companyListRv)
        companyListRv.apply {
            adapter = viewAdapter
            itemAnimator = viewAnimator
            layoutManager = viewManager
            setHasFixedSize(true)
            scrollToPosition(viewAdapter.itemCount - 1)
        }
        return view
    }

    override fun onSearchResult(list: List<Company>) {
        viewAdapter.updateList(list)
        viewAdapter.notifyDataSetChanged()
        companyListRv.scheduleLayoutAnimation()
    }

    override fun onPriceResult(list: List<Company>) {
        viewAdapter.updateList(list)
        viewAdapter.notifyDataSetChanged()
    }

    override fun onWatchListItems(items: List<WatchListItem>) {
        if (items.isNotEmpty()) {
            val favouriteCompanies = items.map { watchListItem ->
                Company(
                    id = watchListItem.id,
                    symbol = watchListItem.symbol,
                    name = watchListItem.name,
                    isFavourite = true
                )
            }
            viewAdapter.updateList(favouriteCompanies)
            viewAdapter.notifyDataSetChanged()
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}