package com.example.lera.ui

import com.example.lera.mvp.BasePresenter

interface SearchPresenter: BasePresenter<SearchView> {
    fun loadCompanies()
    fun search(filter: String)
}