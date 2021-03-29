package com.example.lera.ui

import com.example.lera.data.model.Company
import com.example.lera.mvp.BaseView

interface SearchView: BaseView {
    fun onSearchResult(list: List<Company>)
    fun onPriceResult(list: List<Company>)
}