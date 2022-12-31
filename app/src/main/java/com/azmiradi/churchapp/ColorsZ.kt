package com.azmiradi.churchapp

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ColorsZ(val number: Int, @DrawableRes val colorID: Int) {
    Black(0, R.drawable.black),
    Blue(1, R.drawable.blue),
    Brown(2, R.drawable.brown),
    Gray(4, R.drawable.gray),
    Green(5, R.drawable.green),
    Purple(7, R.drawable.purple),
    Red(8, R.drawable.red),
    Yellow(10, R.drawable.yellow),
    White(12, R.drawable.white),
    Orange(13, R.drawable.orange),
    SkyBlue(14, R.drawable.sky_blue),
}