package com.azmiradi.churchapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import com.azmiradi.churchapp.all_applications.AllApplicationsScreen
import com.azmiradi.churchapp.main_screen.MainScreen
import com.azmiradi.churchapp.ui.theme.ChurchTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        var appLocale: Locale? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            ChurchTheme {
              Navigation()
            }

        }
    }

    init {
        updateConfig(this)
    }

    private fun updateConfig(wrapper: ContextThemeWrapper) {
        if (appLocale == null || appLocale == Locale(""))
            return
        Locale.setDefault(appLocale!!)
        val configuration = Configuration()
        configuration.setLocale(appLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }

}

