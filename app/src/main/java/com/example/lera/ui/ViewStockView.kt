package com.example.lera.ui

import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse
import com.example.lera.mvp.BaseView

interface ViewStockView: BaseView {
    fun onQuoteResult(response: QuoteResponse)
    fun onTimeSeriesResult(response: TimeSeriesResponse)
    fun onItemAddedToWatchList(status: Boolean)
    fun onWatchListItemsExceeded()
    fun onItemInWatchList()
}