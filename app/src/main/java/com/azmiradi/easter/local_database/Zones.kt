package com.azmiradi.easter.local_database

import com.azmiradi.easter.ColorsZ

enum class Zones(val id: Int, val zoneName: String, val code: String, val color: ColorsZ) {
    Zone1(1222321, "رسميين", "T", ColorsZ.White),//
    Zone2(1222322, "مجلس الوزراء", "T", ColorsZ.White),//
    Zone3(1222323, "رؤساء هيئات", "S", ColorsZ.Green),//
    Zone4(1222324, "رؤساء هيئات", "I", ColorsZ.Black),//
    Zone5(1222325, "Diplomats", "R", ColorsZ.Yellow),//
    Zone6(1222326, "هيئات حكومية", "H", ColorsZ.Brown),//
    Zone7(1222327, "شخصيات عامة", "C", ColorsZ.Orange),
    Zone8(1222328, "رؤساء هيئات قضائية", "E", ColorsZ.Grey),//
    Zone9(1222329, "مجلسي النواب والشيوخ", "G", ColorsZ.Red),//
    Zone10(1222330, "رؤساء احزاب واعلاميين", "N", ColorsZ.SkyBlue),//
    Zone11(1222331, "شخصيات عامة", "A", ColorsZ.Purple),//
    Zone12(1222332, "رسميين", "L", ColorsZ.Blue),//
    Zone13(1222333, "امن وطني", "L", ColorsZ.Blue),//
    Zone14(1222334, "امن قومي", "T", ColorsZ.White),//
}