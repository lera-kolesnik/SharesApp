package com.example.lera.ui

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import java.text.SimpleDateFormat

class CustomMarkerPresenter(
    private val datesMap: MutableMap<Float, String>,
    private val setting: ViewStockSetting
) {
    private var view: CustomMarkerView? = null

    fun onRefreshContent(entry: Entry?, highlight: Highlight?) {
        entry?.apply { view?.showPrice("$${String.format("%.2f", this.y)}") }

        val dateStringToDisplay = entry
            ?.let { datesMap[it.x] }
            ?.let { SimpleDateFormat(setting.inputDateFormat).parse(it) }
            ?.let { SimpleDateFormat(setting.outputDateFormat).format(it) }
        dateStringToDisplay?.apply { view?.showDate(dateStringToDisplay) }
    }

    fun attach(view: CustomMarkerView) {
        this.view = view
    }

    fun drop() {
        this.view = null
    }
}
