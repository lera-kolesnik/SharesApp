package com.example.lera.ui

import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.StockUnit
import com.example.lera.mvp.BaseView

interface ViewStockView: BaseView {
    fun onQuoteResult(response: QuoteResponse)
    fun onTimeSeriesResult(
        stockUnits: List<StockUnit>,
        viewStockSetting: ViewStockSetting
    )
    fun onTimeSeriesFailed(errorMessage: String)
    fun onItemAddedToWatchList(status: Boolean)
    fun onWatchListItemsExceeded()
    fun onItemInWatchList()
}