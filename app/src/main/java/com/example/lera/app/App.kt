package com.example.lera.app

import android.app.Application
import com.crazzyghost.alphavantage.AlphaVantage
import com.crazzyghost.alphavantage.Config
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class App : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = AppComponent(this)
        setupAlphavantage()
    }

    private fun setupAlphavantage(){

        val throttlingInterceptor : Interceptor = Interceptor.invoke {
            Thread.sleep(1000)
            it.proceed(it.request())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor = throttlingInterceptor)
            .build()

        val config = Config.Builder()
            .httpClient(client)
            .key("JZE3WVVY6X2FX4UR")
//            .key("P9JDWEC1LNVL1DGM")
            .build()

        AlphaVantage.api().init(config)
    }

}
