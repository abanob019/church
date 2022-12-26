package com.azmiradi.churchapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
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
//        Zones.values().forEach {
//            val data = "عيد الميلادالمجيد ٢٠٢٣" +
//                    "\n" +
//                    it.zoneName +
//                    "\n" +
//                    it.code +
//                    "\n" +
//                    it.color.name +
//                    "\n" + it.id
//
//
//            val qrgEncoder = QRGEncoder(data, null, QRGContents.Type.TEXT, 150)
//            qrgEncoder.getBitmap(0).saveBitmap(
//                "/Invitations/ColorsQRs",
//                it.zoneName + "_" + it.code + "_" + it.color.name,
//                this
//            )
//            {
//                println("DADADAD : " + it)
//            }
//        }
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



enum class Zones(val id: Int, val zoneName: String, val code: String, val color: ColorsZ) {
    Zone1(1222321, "رسميين", "T", ColorsZ.White),//
    Zone2(1222322, "مجلس الوزراء", "T", ColorsZ.White),//
    Zone3(1222323, "رؤساء هيئات", "S", ColorsZ.Green),//
    Zone4(1222324, "رؤساء هيئات", "I", ColorsZ.Black),//
    Zone5(1222325, "Diplomats", "R", ColorsZ.Yellow),//
    Zone6(1222326, "هيئات حكومية", "H", ColorsZ.Brown),//
    Zone7(1222327, "شخصيات عامة", "C", ColorsZ.Orange),
    Zone8(1222328, "رؤساء هيئات قضائية", "E", ColorsZ.Gray),//
    Zone9(1222329, "مجلسي النواب والشيوخ", "G", ColorsZ.Red),//
    Zone10(1222330, "رؤساء احزاب واعلاميين", "N", ColorsZ.SkyBlue),//
    Zone11(1222331, "شخصيات عامة", "A", ColorsZ.Purple),//
    Zone12(1222332, "رسميين", "L", ColorsZ.Blue),//
}