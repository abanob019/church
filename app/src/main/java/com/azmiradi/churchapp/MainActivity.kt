package com.azmiradi.churchapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
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
                //MyContent()
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

@Composable
fun MyContent(){

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl("file:///android_asset/invitation.html")
        }
    }, update = {
        it.loadUrl("file:///android_asset/invitation.html")

    })
}