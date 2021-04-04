package com.example.lera.data.repo

import com.crazzyghost.alphavantage.AlphaVantage
import com.crazzyghost.alphavantage.AlphaVantageException
import com.crazzyghost.alphavantage.Fetcher
import com.crazzyghost.alphavantage.parameters.OutputSize
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse
import com.example.lera.data.DatabaseManager
import com.example.lera.data.model.Company
import com.example.lera.data.model.WatchListItem
import com.example.lera.data.model.WatchListItem_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

/**
 * A database proxy to access 'watched' items.
 */
class WatchListRepository(database: DatabaseManager){

    private val box: Box<WatchListItem> = database.boxStore().boxFor()

    fun count(): Long {
        return box.count()
    }

    fun find(company: Company) : WatchListItem? {
        return box.query()
            .equal(WatchListItem_.name, company.name)
            .and()
            .equal(WatchListItem_.symbol, company.symbol)
            .build()
            .findFirst()
    }

    fun save(company: Company, quote: QuoteResponse?) {
        val existing = find(company)
        if(existing != null) {
            existing.previousClose = quote?.previousClose
            existing.open = quote?.open
            existing.high = quote?.high
            existing.low = quote?.low
            existing.volume = quote?.volume
            existing.change = quote?.changePercent
            box.put(existing)
            return
        }
        val item =  WatchListItem(
            name = company.name,
            symbol = company.symbol,
            previousClose = quote?.previousClose,
            open = quote?.open,
            high = quote?.high,
            low = quote?.low,
            volume = quote?.volume,
            change = quote?.changePercent
        )
        box.put(item)
    }

    fun exists(company: Company): Boolean {
        return find(company) != null
    }

    fun all() : List<WatchListItem> {
        return box.all
    }

    fun delete(item: Company) {
        box.query()
            .equal(WatchListItem_.name, item.name)
            .and()
            .equal(WatchListItem_.symbol, item.symbol)
            .build()
            .remove()
    }

    fun getQuote(
        symbol: String?,
        onSuccessCallback: Fetcher.SuccessCallback<QuoteResponse>,
        onFailureCallback: Fetcher.FailureCallback
    ) {
        try {
            AlphaVantage.api()
                .timeSeries()
                .quote()
                .forSymbol(symbol)
                .onSuccess(onSuccessCallback)
                .onFailure(onFailureCallback)
                .fetch()
        } catch (e: IndexOutOfBoundsException) {
            onFailureCallback.onFailure(AlphaVantageException(e.message))
        }
    }

    fun getDaily(
        symbol: String?,
        onSuccessCallback: Fetcher.SuccessCallback<TimeSeriesResponse>,
        onFailureCallback: Fetcher.FailureCallback
    ) {
        AlphaVantage.api()
            .timeSeries()
            .daily()
            .outputSize(OutputSize.COMPACT)
            .forSymbol(symbol)
            .onSuccess(onSuccessCallback)
            .onFailure(onFailureCallback)
            .fetch()
    }

    fun getWeekly(
        symbol: String?,
        onSuccessCallback: Fetcher.SuccessCallback<TimeSeriesResponse>,
        onFailureCallback: Fetcher.FailureCallback
    ) {

        AlphaVantage.api()
            .timeSeries()
            .weekly()
            .forSymbol(symbol)
            .onSuccess(onSuccessCallback)
            .onFailure(onFailureCallback)
            .fetch()
    }

    fun getMonthly(
        symbol: String?,
        onSuccessCallback: Fetcher.SuccessCallback<TimeSeriesResponse>,
        onFailureCallback: Fetcher.FailureCallback
    ) {
        AlphaVantage.api()
            .timeSeries()
            .monthly()
            .forSymbol(symbol)
            .onSuccess(onSuccessCallback)
            .onFailure(onFailureCallback)
            .fetch()
    }


    fun getIntraday(
        symbol: String?,
        onSuccessCallback: Fetcher.SuccessCallback<TimeSeriesResponse>,
        onFailureCallback: Fetcher.FailureCallback
    ) {

        AlphaVantage.api()
            .timeSeries()
            .intraday()
            .forSymbol(symbol)
            .onSuccess(onSuccessCallback)
            .onFailure(onFailureCallback)
            .fetch()
    }
}