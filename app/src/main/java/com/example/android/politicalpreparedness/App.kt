package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber
import timber.log.Timber.Forest.plant

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        } else {
            // TODO: If Firebase Crashlytics is available, replace with a CrashReportingTree here
            plant(Timber.DebugTree())
        }
    }

}