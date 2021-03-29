package com.example.lera.app

import android.app.Application
import com.example.lera.data.AppDatabaseManager
import com.example.lera.data.DatabaseManager
import com.example.lera.data.repo.CompanyRepository
import com.example.lera.data.repo.WatchListRepository
import com.example.lera.ui.SearchPresenterImpl
import com.example.lera.ui.ViewStockPresenterImpl

/**
 * Holds dependencies that are shared for necessary views.
 */
class AppComponent(app: Application){

    private val database: DatabaseManager = AppDatabaseManager(app.applicationContext)
    private val companyRepository = CompanyRepository(database)
    private val watchListRepository = WatchListRepository(database)


    fun searchPresenter() : SearchPresenterImpl {
        return SearchPresenterImpl(companyRepository, watchListRepository)
    }

    fun stockPresenter(): ViewStockPresenterImpl {
        return ViewStockPresenterImpl(watchListRepository)
    }
}