package com.azmiradi.easter.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.azmiradi.easter.R



private val myCustomFont = FontFamily(
    Font(R.font.medium),
)


val Typography = Typography(
    defaultFontFamily = myCustomFont,
)