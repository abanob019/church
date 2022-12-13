package com.azmiradi.churchapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.*

const val ARABIC_LANGUAGE = "ar"

@HiltAndroidApp
class App: Application()
{
    override fun onCreate() {
        super.onCreate()
        MainActivity.appLocale = Locale(ARABIC_LANGUAGE)

    }
}