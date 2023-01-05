package com.azmiradi.churchapp

import android.app.Application
import com.rollbar.android.Rollbar
import dagger.hilt.android.HiltAndroidApp
import java.util.*

const val ARABIC_LANGUAGE = "ar"

@HiltAndroidApp
class App: Application()
{
    override fun onCreate() {
        super.onCreate()
        Rollbar.init(this)
        MainActivity.appLocale = Locale(ARABIC_LANGUAGE)
    }
}