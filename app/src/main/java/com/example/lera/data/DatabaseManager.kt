package com.example.lera.data

import io.objectbox.BoxStore

interface DatabaseManager {

    fun setupCompanyList()
    fun boxStore(): BoxStore
}