package com.azmiradi.invitations.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.azmiradi.invitations.R



private val myCustomFont = FontFamily(
    Font(R.font.medium),
)


val Typography = Typography(
    defaultFontFamily = myCustomFont,
)