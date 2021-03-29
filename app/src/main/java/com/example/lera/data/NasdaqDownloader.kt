package com.example.lera.data

import java.io.IOException
import java.net.URL

object NasdaqDownloader {

    fun download(): String? {
        val link = "http://nasdaqtrader.com/dynamic/SymDir/nasdaqlisted.txt"
        return try {
            URL(link).readText()
        } catch (e: IOException){
            null
        }
    }
}