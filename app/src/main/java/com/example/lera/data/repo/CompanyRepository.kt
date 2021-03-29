package com.example.lera.data.repo

import android.util.Log
import com.example.lera.data.DatabaseManager
import com.example.lera.data.model.Company
import com.example.lera.data.model.Company_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

class CompanyRepository(private val database: DatabaseManager) {

    private val box: Box<Company> = database.boxStore().boxFor()
    private var latestQuery: String? = null

    fun loadCompanies(): List<Company>? {
        if (database.boxStore().boxFor(Company::class.java).isEmpty) {
            database.setupCompanyList()
        }
        return latestQuery?.let {
            search(it)
        } ?: DEFAULT_SYMBOLS
            .map { defaultSymbol -> search(defaultSymbol) }
            .filter { it != null && it.isNotEmpty() }
            .map { it!!.first() }
    }


    fun search(query: String): List<Company>? {
        return if (database.boxStore().boxFor(Company::class.java).isEmpty) {
            // Keep query until better time when we load companies
            latestQuery = query
            null
        } else {
            val result = box.query()
                .startsWith(Company_.name, query)
                .or()
                .startsWith(Company_.symbol, query)
                .build()
                .find()
                .take(10)
            Log.wtf("CompanyRepository", "$result")
            result
        }
    }

    companion object {
        val DEFAULT_SYMBOLS = listOf("YNDX", "AAPL", "GOOGL", "AMZN", "MSFT", "TSLA")
    }
}