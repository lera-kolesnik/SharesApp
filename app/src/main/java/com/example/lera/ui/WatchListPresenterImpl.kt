package com.example.lera.ui

import com.example.lera.data.model.Company
import com.example.lera.data.repo.WatchListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WatchListPresenterImpl(
    private var repository: WatchListRepository
) : WatchListPresenter {

    private var view: WatchListView? = null

    override fun attach(view: WatchListView) {
        this.view = view
    }

    override fun drop(view: WatchListView) {
        this.view = null
    }

    override fun getWatchListItems() {
        GlobalScope.launch(Dispatchers.IO) {
            val list = repository.all()
            launch(Dispatchers.Main){
                view?.onWatchListItems(list)
            }
        }
    }

    override fun deleteItem(item: Company, adapterPosition: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.delete(item)
            val list = repository.all()
            launch(Dispatchers.Main) {
                view?.onWatchListItems(list)
            }
        }
    }

    override fun addToWatchList(company: Company) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.save(company, null)
            val list = repository.all()
            launch(Dispatchers.Main) {
                view?.onWatchListItems(list)
            }
        }
    }

}