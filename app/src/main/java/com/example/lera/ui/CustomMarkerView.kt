package com.example.lera.ui

import android.content.Context
import android.widget.TextView
import com.example.lera.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class CustomMarkerView(
    context: Context,
    layoutRes: Int,
    private val presenter: CustomMarkerPresenter
) : MarkerView(context, layoutRes) {

    fun showPrice(price: String) {
        val priceView: TextView = findViewById(R.id.price)
        priceView.text = price
    }

    fun showDate(dateString: String) {
        val dateView: TextView = findViewById(R.id.date)
        dateView.text = dateString
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        presenter.attach(this)
        presenter.onRefreshContent(entry, highlight)
    }

    override fun onDetachedFromWindow() {
        presenter.drop()
        super.onDetachedFromWindow()
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF((-width / 2).toFloat(), (-height * 1.2).toFloat())
    }
}
