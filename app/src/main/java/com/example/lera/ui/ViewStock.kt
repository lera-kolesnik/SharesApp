package com.example.lera.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse
import com.example.lera.R
import com.example.lera.app.App
import com.example.lera.data.model.Company
import com.example.lera.util.Constants.EXTRA_STOCK_NAME
import com.example.lera.util.Constants.EXTRA_STOCK_SYMBOL
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.DecimalFormat
import javax.inject.Inject

class ViewStock : AppCompatActivity(), ViewStockView {

    @Inject
    lateinit var presenter: ViewStockPresenter
    var name: String? = ""
    var symbol: String? = ""
    private val fmt = DecimalFormat("#,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock)
        presenter = (applicationContext as App).component.stockPresenter()
        name = intent.getStringExtra(EXTRA_STOCK_NAME)
        symbol = intent.getStringExtra(EXTRA_STOCK_SYMBOL)
        initUi()
    }

    private fun initUi(){
        presenter.fetchIntraday(symbol)
        presenter.fetchQuote(symbol)

        val companyNameTv = findViewById<TextView>(R.id.companyNameTv)
        val companySymbolTv = findViewById<TextView>(R.id.companySymbolTv)
        val graph = findViewById<LineChart>(R.id.graph)

        companyNameTv.text = name
        companySymbolTv.text = symbol
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
        graph.fitScreen()

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.drop()
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
            val sign : String = if (response.changePercent > 0) "+" else ""
            percentChangeTv.text =  sign + fmt.format(response.changePercent) + "%"

            val resource: Int = if (response.changePercent > 0)
                R.drawable.background_positive_change else R.drawable.background_negative_change

            percentChangeTv.setBackgroundResource(resource)
            presenter.updateIfInWatchList(Company(name = name, symbol = symbol))

        } else{
            Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onTimeSeriesResult(response: TimeSeriesResponse) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val graph = findViewById<LineChart>(R.id.graph)
        progressBar.visibility = View.GONE
        if (response.errorMessage == null) {
            val entries = mutableListOf<Entry>()
            for (i in (response.stockUnits.size - 1) downTo 0) {
                entries.add(Entry(
                        (response.stockUnits.size - i).toFloat(),
                        response.stockUnits[i].high.toFloat()))
            }
            val dataSet = LineDataSet(entries, "")
            dataSet.lineWidth = 2f
            dataSet.setDrawCircles(false)
            dataSet.setDrawValues(false)
            dataSet.resetColors()
            dataSet.color = ContextCompat.getColor(applicationContext, R.color.stock_title_color)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            val data = LineData(dataSet)
            graph.data = data
            graph.invalidate()
        } else {
            Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemAddedToWatchList(status: Boolean) {
        val state = if (status) " " else " not "
        Toast.makeText(this, "Item" + state + "added to watchlist", Toast.LENGTH_LONG).show()
    }

    override fun onWatchListItemsExceeded() {
        Toast.makeText(this, "You can only watch up to 5 companies", Toast.LENGTH_LONG).show()
    }

    override fun onItemInWatchList() {
        Toast.makeText(this, "You are watching this item", Toast.LENGTH_LONG).show()
    }
}
