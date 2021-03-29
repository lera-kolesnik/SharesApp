package com.example.lera.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lera.R
import com.example.lera.adapter.CompanyListAdapter
import com.example.lera.app.App
import com.example.lera.data.model.Company
import com.example.lera.util.ClickListener
import com.example.lera.util.Constants.EXTRA_STOCK_NAME
import com.example.lera.util.Constants.EXTRA_STOCK_SYMBOL
import com.example.lera.util.ItemTouchListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Holds logic to display Search view with list of stocks
 */
class Search : AppCompatActivity(), SearchView, CoroutineScope {

    lateinit var searchEt: EditText
    lateinit var companyListRv: RecyclerView
    lateinit var presenter: SearchPresenter
    lateinit var viewAdapter: CompanyListAdapter
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAnimator: RecyclerView.ItemAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        presenter = (applicationContext as App).component.searchPresenter()
        presenter.loadCompanies()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.drop()
    }

    private fun initUi(){
        viewAdapter = CompanyListAdapter(this)
        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL ,false)
        viewAnimator = DefaultItemAnimator()
        companyListRv = findViewById(R.id.companyListRv)
        companyListRv.apply {
            adapter = viewAdapter
            itemAnimator = viewAnimator
            layoutManager = viewManager
            setHasFixedSize(true)
            scrollToPosition(viewAdapter.itemCount - 1)
        }
        companyListRv.addOnItemTouchListener(ItemTouchListener(this, companyListRv, object:
            ClickListener {
            override fun onClick(view: View?, position: Int) {
                val company = viewAdapter.get(position)
                val intent = Intent(this@Search, ViewStock::class.java)
                intent.putExtra(EXTRA_STOCK_NAME, company.name)
                intent.putExtra(EXTRA_STOCK_SYMBOL, company.symbol)
                startActivity(intent)
            }
            override fun onLongClick(view: View?, position: Int) = Unit
        }))
        val watcher = object : TextWatcher {
            private var searchFor = ""
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == "") {
                    onSearchResult(listOf())
                    return
                }
                if (searchText == searchFor) return
                searchFor = searchText
                launch {
                    delay(1000)
                    if (searchText != searchFor) return@launch
                    presenter.search(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) =  Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        }
        searchEt = findViewById(R.id.searchEt)
        searchEt.addTextChangedListener(watcher)
    }

    override fun onSearchResult(list: List<Company>) {
        viewAdapter.updateList(list)
        viewAdapter.notifyDataSetChanged()
        companyListRv.scheduleLayoutAnimation()
    }

    override fun onPriceResult(list: List<Company>) {
        viewAdapter.updateList(list)
        viewAdapter.notifyDataSetChanged()
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}
