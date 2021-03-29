package com.example.lera.data

interface CompaniesDownloadCallback {
    fun onSuccess()
    fun onFailure(error: String?)
}