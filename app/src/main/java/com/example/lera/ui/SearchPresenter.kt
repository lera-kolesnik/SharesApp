package com.example.lera.ui

import com.example.lera.data.model.Company
import com.example.lera.mvp.BasePresenter

public interface SearchPresenter: BasePresenter<SearchView> {
    fun loadCompanies()
    fun search(filter: String)
}