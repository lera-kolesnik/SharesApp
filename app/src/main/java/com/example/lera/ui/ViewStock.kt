package com.example.lera.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.StockUnit
import com.example.lera.R
import com.example.lera.app.App
import com.example.lera.data.model.Company
import com.example.lera.ui.ViewStockSetting.*
import com.example.lera.util.Constants.EXTRA_STOCK_NAME
import com.example.lera.util.Constants.EXTRA_STOCK_SYMBOL
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.DecimalFormat

/**
 * Holds view logic to present details about stock.
 */
class ViewStock : AppCompatActivity(), ViewStockView {

    lateinit var presenter: ViewStockPresenter
    var name: String? = ""
    var symbol: String? = ""
    private val fmt = DecimalFormat("#,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_stock_layout)
        presenter = (applicationContext as App).component.stockPresenter()
        name = intent.getStringExtra(EXTRA_STOCK_NAME)
        symbol = intent.getStringExtra(EXTRA_STOCK_SYMBOL)
        initUi()
    }

    private fun initUi(){
        presenter.fetchIntraday(symbol)
        presenter.fetchQuote(symbol)

        findViewById<TextView>(R.id.companyNameTv).text = name
        findViewById<TextView>(R.id.companySymbolTv).text = symbol

        val graph = findViewById<LineChart>(R.id.graph)
        graph.setNoDataText("")
        graph.setPinchZoom(true)
        graph.axisRight.isEnabled = false
        graph.axisLeft.isEnabled = false
        graph.axisLeft.setDrawAxisLine(false)
        graph.axisLeft.setDrawTopYLabelEntry(false)
        graph.axisLeft.setDrawLabels(false)
        graph.xAxis.setDrawLabels(false)
        graph.xAxis.isEnabled = false
        graph.legend.isEnabled = false
        graph.description.isEnabled = false
        graph.setDrawGridBackground(false)
        graph.setBackgroundColor(getColor(R.color.white))
        graph.fitScreen()
        graph.setExtraOffsets(25.0F, 50.0F, 25.0F, 0.0F)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        findViewById<Button>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<Button>(R.id.inner_day).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            graph.clear()
            presenter.fetchIntraday(symbol)
        }
        findViewById<Button>(R.id.weekly).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            graph.clear()
            presenter.fetchIntraWeekly(symbol)
        }
        findViewById<Button>(R.id.monthly).setOnClickListener {
            graph.clear()
            progressBar.visibility = View.VISIBLE
            presenter.fetchIntraMonthly(symbol)
        }
        findViewById<Button>(R.id.all).setOnClickListener {
            graph.clear()
            progressBar.visibility = View.VISIBLE
            presenter.fetchAll(symbol)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.drop(this)
    }

    override fun onQuoteResult(response: QuoteResponse) {
        if (response.errorMessage == null) {
            val previousCloseTv = findViewById<TextView>(R.id.previousCloseTv)
            val openTv = findViewById<TextView>(R.id.openTv)
            val highTv = findViewById<TextView>(R.id.highTv)
            val lowTv = findViewById<TextView>(R.id.lowTv)
            val volumeTv = findViewById<TextView>(R.id.volumeTv)
            val bannerHighTv = findViewById<TextView>(R.id.bannerHighTv)
            val percentChangeTv = findViewById<TextView>(R.id.percentChangeTv)

            previousCloseTv.text = fmt.format(response.previousClose)
            openTv.text = fmt.format(response.open)
            highTv.text = fmt.format(response.high)
            lowTv.text = fmt.format(response.low)
            volumeTv.text = fmt.format(response.volume)
            bannerHighTv.text = fmt.format(response.high)

            val changePercent = response.changePercent
            val isPositive = changePercent > 0
            val isNeural = changePercent == 0.0
            val isNegative = !isPositive and !isNeural
            percentChangeTv.setTextColor(
                ContextCompat.getColor(
                    baseContext,
                    when {
                        isPositive -> R.color.green
                        isNeural -> R.color.grey
                        else -> R.color.red
                    }
                )
            )
            val sign = if (isNeural || isNegative) "" else "+"
            percentChangeTv.text = "$sign$changePercent%"

            presenter.updateIfInWatchList(Company(name = name, symbol = symbol))

        } else{
            Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateStockSettingButtons(viewStockSetting: ViewStockSetting) {
        arrayOf(R.id.inner_day, R.id.weekly, R.id.monthly).forEach {
            // Reset button colours:
            findViewById<Button>(it).setBackgroundColor(getColor(R.color.grey))
            findViewById<Button>(it).setTextColor(getColor(R.color.colorPrimary))
        }
        val activatedButtonId = when (viewStockSetting) {
            INTRA_DAY -> R.id.inner_day
            INTRA_WEEKLY -> R.id.weekly
            INTRA_MONTHLY ->  R.id.monthly
            ALL -> R.id.all
        }
        findViewById<Button>(activatedButtonId).setBackgroundColor(getColor(R.color.colorPrimary))
        findViewById<Button>(activatedButtonId).setTextColor(getColor(R.color.colorPrimaryDark))
    }

    override fun onTimeSeriesResult(
        stockUnits: List<StockUnit>,
        viewStockSetting: ViewStockSetting
    ) {
        updateStockSettingButtons(viewStockSetting)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val graph = findViewById<LineChart>(R.id.graph)
        progressBar.visibility = View.GONE

        val datesMap = mutableMapOf<Float, String>()
        val entries = mutableListOf<Entry>()
        for (i in (stockUnits.size - 1) downTo 0) {
            val x = (stockUnits.size - i).toFloat()
            datesMap[x] = stockUnits[i].date
            val y = stockUnits[i].high.toFloat()
            entries.add(Entry(x, y))
        }
        val dataSet = LineDataSet(entries, "")
        dataSet.fillDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(getColor(R.color.colorPrimary), getColor(R.color.colorPrimaryDark))
        ).apply { this.alpha = 25 }
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.resetColors()
        dataSet.color = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val data = LineData(dataSet)
        graph.data = data
        graph.marker = CustomMarkerView(
            applicationContext,
            R.layout.graph_marker_layout,
            CustomMarkerPresenter(datesMap, viewStockSetting)
        )
        graph.invalidate()
    }

    override fun onTimeSeriesFailed(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onItemAddedToWatchList(status: Boolean) {

    }

    override fun onWatchListItemsExceeded() {

    }

    override fun onItemInWatchList() {

    }
}
