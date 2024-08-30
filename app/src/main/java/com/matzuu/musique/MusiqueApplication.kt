package com.matzuu.musique

import android.app.Application
import com.matzuu.musique.database.AppContainer
import com.matzuu.musique.database.DefaultAppContainer

class MusiqueApplication : Application() {
    lateinit var container : AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}