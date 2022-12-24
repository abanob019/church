package com.azmiradi.churchapp

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ColorsZ(val number: Int, @DrawableRes val colorID: Int,val color:Color) {
    Black(0, R.drawable.black, Color(0xff000000)),
    Blue(1, R.drawable.blue, Color(0xff000080)),
    Brown(2, R.drawable.brouwn,Color(0xff7f6000)),
    Cerulean(3, R.drawable.cerulean,Color(0xff0F4C81)),
    Gray(4, R.drawable.gray,Color(0xff808080)),
    Green(5, R.drawable.green,Color(0xff4E9B47)),
    Misty(6, R.drawable.misty,Color(0xffCBB1B1)),
    Purple(7, R.drawable.purple,Color(0xff800080)),
    Red(8, R.drawable.red,Color(0xffC70039)),
    Umber(9, R.drawable.umber,Color(0xff5D4A44)),
    Yellow(10, R.drawable.yellow,Color(0xffFFBF00)),
    CadetBlue(11, R.drawable.cadetblue,Color(0xff5F9EA0)),
    White(12, R.drawable.white,Color(0xffffffff)),

    Orange(12, R.drawable.orang,Color(0xffffffff)),
    Pink(13, R.drawable.pink,Color(0xffffffff)),
    SkyBlue(14, R.drawable.pink,Color(0xffffffff)),

}