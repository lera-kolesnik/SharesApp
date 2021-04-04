package com.example.lera.ui

import com.example.lera.data.model.Company
import com.example.lera.mvp.BasePresenter

interface WatchListPresenter : BasePresenter<WatchListView> {
    fun getWatchListItems()
    fun deleteItem(item: Company, adapterPosition: Int)
    fun addToWatchList(company: Company)
}
