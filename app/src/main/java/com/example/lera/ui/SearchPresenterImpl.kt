package com.example.lera.ui

import com.crazzyghost.alphavantage.Fetcher
import com.example.lera.data.model.Company
import com.example.lera.data.repo.CompanyRepository
import com.example.lera.data.repo.WatchListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.round

/**
 * Implements presenter interface to serve Search view.
 */
class SearchPresenterImpl(
    private var companyRepository: CompanyRepository,
    private var watchListRepository: WatchListRepository
): SearchPresenter {

    var view: SearchView? = null
    private var searchTerm = ""

    override fun loadCompanies() {
        GlobalScope.launch(Dispatchers.IO) {
            companyRepository.loadCompanies()?.let { companies ->
                fetchQuoteForCompanies(companies)
                GlobalScope.launch(Dispatchers.Main) { view?.onSearchResult(companies) }
            }
        }
    }

    override fun search(filter: String) {
        searchTerm = filter
        if (searchTerm == "") {
            view?.onSearchResult(listOf())
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            companyRepository.search(filter)?.let { companies ->
                fetchQuoteForCompanies(companies)
                GlobalScope.launch(Dispatchers.Main) { view?.onSearchResult(companies) }
            }
        }
    }

    private fun fetchQuoteForCompanies(list: List<Company>) {
        list.forEach { company ->
            watchListRepository.getQuote(
                company.symbol,
                Fetcher.SuccessCallback { r ->
                    val stockToUpdate = list.find { it.id == company.id }
                    stockToUpdate?.price = r.price.round(2)
                    stockToUpdate?.priceChange = r.change.round(2)
                    stockToUpdate?.changePercent = r.changePercent.round(2)
                    GlobalScope.launch(Dispatchers.Main) { view?.onPriceResult(list) }
                },
                Fetcher.FailureCallback { e -> println(e.message) }
            )
        }
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    override fun attach(view: SearchView) {
        this.view = view
    }

    override fun drop() {
        this.view = null
    }
}
