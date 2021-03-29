package com.example.lera.mvp

interface BasePresenter<T> {
    fun attach(view: T)
    fun drop()
}