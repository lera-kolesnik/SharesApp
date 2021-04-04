package com.example.lera.ui

import com.crazzyghost.alphavantage.AlphaVantageException
import com.crazzyghost.alphavantage.Fetcher
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.StockUnit
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse
import com.example.lera.data.model.Company
import com.example.lera.data.repo.WatchListRepository
import com.example.lera.ui.ViewStockSetting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ViewStockPresenterImpl(
    private var repository: WatchListRepository
): ViewStockPresenter {

    var view: ViewStockView? = null
    private var quote: QuoteResponse? = null

    override fun attach(view: ViewStockView) {
        this.view = view
    }

    override fun drop(view: ViewStockView) {
        this.view = null
    }

    override fun fetchQuote(symbol: String?) {
        repository.getQuote(
            symbol,
            Fetcher.SuccessCallback { r -> onQuoteResponse(r) },
            Fetcher.FailureCallback { e -> onError(e) }
        )
    }

    override fun fetchAll(symbol: String?) {
        repository.getMonthly(
            symbol,
            Fetcher.SuccessCallback { r -> onTimeSeriesResponse(r, ALL) },
            Fetcher.FailureCallback { e -> onError(e) }
        )
    }

    override fun fetchIntraMonthly(symbol: String?) {
        repository.getDaily(
            symbol,
            Fetcher.SuccessCallback { r -> onTimeSeriesResponse(r, INTRA_MONTHLY) },
            Fetcher.FailureCallback { e -> onError(e) }
        )
    }

    override fun fetchIntraWeekly(symbol: String?) {
        repository.getDaily(
            symbol,
            Fetcher.SuccessCallback { r -> onTimeSeriesResponse(r, INTRA_WEEKLY) },
            Fetcher.FailureCallback { e -> onError(e) }
        )
    }

    override fun fetchIntraday(symbol: String?) {
        repository.getIntraday(
            symbol,
            Fetcher.SuccessCallback { r -> onTimeSeriesResponse(r, INTRA_DAY) },
            Fetcher.FailureCallback { e -> onError(e) }
        )
    }


    override fun addToWatchList(company: Company){

        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (repository.count() == 5L) {
                    launch(Dispatchers.Main) {
                        view?.onWatchListItemsExceeded()
                    }
                    return@launch
                }
                if (repository.exists(company)) {
                    launch(Dispatchers.Main) {
                        view?.onItemInWatchList()
                    }
                    return@launch
                }
                repository.save(company, quote)
                launch(Dispatchers.Main){
                    view?.onItemAddedToWatchList(true)
                }

            } catch(e: Exception) {
                launch(Dispatchers.Main){
                    view?.onItemAddedToWatchList(false)
                }
            }
        }

    }

    private fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.time
    }

    private fun getMonthsAgo(monthsAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -monthsAgo)
        return calendar.time
    }

    private fun onTimeSeriesResponse(
        response: TimeSeriesResponse,
        viewStockSetting: ViewStockSetting
    ){
        if (response.errorMessage == null) {
            val valuesPredicate: (StockUnit) -> Boolean = when(viewStockSetting) {
                INTRA_WEEKLY -> { unit ->
                    SimpleDateFormat(viewStockSetting.inputDateFormat)
                        .parse(unit.date)
                        .after(getDaysAgo(daysAgo = 7))
                }
                INTRA_MONTHLY -> { unit ->
                    SimpleDateFormat(viewStockSetting.inputDateFormat)
                        .parse(unit.date)
                        .after(getMonthsAgo(monthsAgo = 1))
                }
                INTRA_DAY, ALL -> { _ -> true }
            }
            val filteredStockValues = response.stockUnits.filter(valuesPredicate)
            GlobalScope.launch(Dispatchers.Main){
                view?.onTimeSeriesResult(filteredStockValues, viewStockSetting)
            }
        } else {
            GlobalScope.launch(Dispatchers.Main){
                view?.onTimeSeriesFailed(response.errorMessage)
            }
        }
    }

    private fun onQuoteResponse(response: QuoteResponse){
        quote = response
        GlobalScope.launch(Dispatchers.Main){
            view?.onQuoteResult(response)
        }
    }

    private fun onError(exception: AlphaVantageException){
        println(exception.message)
    }

    override fun updateIfInWatchList(company: Company){
        GlobalScope.launch(Dispatchers.IO) {
            if (repository.exists(company)) {
                repository.save(company, quote)
            }
        }
    }

}