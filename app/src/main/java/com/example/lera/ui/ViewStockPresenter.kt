package com.example.lera.ui

import com.example.lera.data.model.Company
import com.example.lera.mvp.BasePresenter

interface ViewStockPresenter: BasePresenter<ViewStockView> {
    fun fetchIntraday(symbol: String?)
    fun fetchIntraMonthly(symbol: String?)
    fun fetchIntraWeekly(symbol: String?)
    fun fetchAll(symbol: String?)
    fun fetchQuote(symbol: String?)
    fun addToWatchList(company: Company)
    fun updateIfInWatchList(company: Company)
}
