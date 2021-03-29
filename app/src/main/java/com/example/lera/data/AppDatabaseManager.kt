package com.example.lera.data

import android.content.Context
import android.util.Log
import com.example.lera.data.model.Company
import com.example.lera.data.model.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Database object that stores necessary objects with a help of [BoxStore]
 */
class AppDatabaseManager(var context : Context): DatabaseManager {

     var boxStore: BoxStore
         private set

    init {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }


    override fun setupCompanyList() {
        GlobalScope.launch (Dispatchers.IO) {
            val result = NasdaqDownloader.download()
            result?.let {
                insertCompaniesIntoDatabase(result)
            }
        }
    }

    private fun insertCompaniesIntoDatabase(result: String){
        val companyBox: Box<Company> = boxStore.boxFor()
        companyBox.removeAll()
        val lines = result.split("\n")
        for(n in 1 until lines.size){
            val components = lines[n].split("|")
            try {
                companyBox.put(
                    Company(
                        symbol = components[0],
                        name = components[1].split("-")[0]
                    )
                )
            } catch (e: IndexOutOfBoundsException){
                e.printStackTrace()
            }
        }
        companyBox.put(
            Company(
                symbol = "YNDX",
                name = "Yandex, LLC"
            )
        )
    }

    override fun boxStore(): BoxStore {
        return boxStore
    }
}