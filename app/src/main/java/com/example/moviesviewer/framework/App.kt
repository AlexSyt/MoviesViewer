package com.example.moviesviewer.framework

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Setup Koin
        startKoin {
            androidContext(applicationContext)
            androidLogger()
            modules(appModule)
        }
    }
}