package com.example.lera.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.URL


//https://eodhistoricaldata.com/img/logos/US/MSFT.png

object CompanyIconDownloader {

    fun download(callback: DownloadIconCallback) {
        val link = "https://eodhistoricaldata.com/img/logos/US/MSFT.png"
        try{
            val data = URL(link).readBytes()

            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
            callback.onSuccess(bmp)
        }catch (e: IOException){
            callback.onFailure(e.message)
        }
    }

    interface DownloadIconCallback{
        fun onSuccess(result: Bitmap)
        fun onFailure(error: String?)
    }
}