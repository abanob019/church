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
import androidx.compose.runtime.Composable
import com.azmiradi.churchapp.application_details.saveBitmap
import com.azmiradi.churchapp.ui.theme.ChurchTheme
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.StandardCharsets
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        var appLocale: Locale? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val options = QrOptions.Builder(1024)
            .setColors(
                QrColors(
                    dark = QrColor.Solid(getColor(R.color.black)),
                    highlighting = QrColor.Solid(0xddffffff.toColor()),
                )
            ).setElementsShapes(
                QrElementsShapes(
                    darkPixel = QrPixelShape.RoundCorners(),
                    ball = QrBallShape.RoundCorners(.25f),
                    frame = QrFrameShape.RoundCorners(.25f),
                )
            ).setPadding(0f).build()

        val generator = QrCodeGenerator(this)
        generator.generateQrCode(
            QrData.Url("https://forms.gle/5jvCEzNtkzRQ5h5C9"), options, StandardCharsets.UTF_8
        ).saveBitmap(
            "/Invitations/ColorsQRs",
            "otograph",
            this
        )
        {
            println("DADADAD : " + it)
        }
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