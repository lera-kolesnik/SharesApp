package com.example.lera.mvp

import com.example.lera.ui.SearchView

interface BasePresenter<T> {
    fun attach(view: T)
    fun drop(view: T)
}