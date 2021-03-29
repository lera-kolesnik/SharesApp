package com.example.lera.data.repo

import com.example.lera.data.DatabaseManager
import com.example.lera.data.model.Company
import com.example.lera.data.model.Company_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor

/**
 * A database proxy to load and search company queries.
 */
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
            result
        }
    }

    companion object {
        const val YNDX = "YNDX"
        const val AAPL = "AAPL"
        const val GOOGL = "GOOGL"
        const val AMZN = "AMZN"
        const val MSFT = "MSFT"
        const val TSLA = "TSLA"
        val DEFAULT_SYMBOLS = listOf(YNDX, AAPL, GOOGL, AMZN, MSFT, TSLA)
    }
}