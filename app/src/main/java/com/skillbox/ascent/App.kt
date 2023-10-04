package com.skillbox.ascent

import android.app.Application
import com.skillbox.ascent.data.db.Database
import com.skillbox.ascent.utils.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.init(this)
        NotificationChannels.create(this)
        // Timber.plant(Timber.DebugTree())
    }
}