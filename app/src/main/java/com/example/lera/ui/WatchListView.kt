package com.example.lera.ui

import com.example.lera.data.model.WatchListItem
import com.example.lera.mvp.BaseView

interface WatchListView: BaseView {
    fun onWatchListItems(items: List<WatchListItem>)
}
