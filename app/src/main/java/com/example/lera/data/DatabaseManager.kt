package com.example.lera.data

import io.objectbox.BoxStore

/**
 * Interface for a database implementation.
 */
interface DatabaseManager {

    fun setupCompanyList()
    fun boxStore(): BoxStore
}