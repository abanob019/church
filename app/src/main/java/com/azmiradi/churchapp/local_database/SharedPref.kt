package com.azmiradi.churchapp.local_database

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


object PreferenceHelper {

    private const val IS_OFFLINE = "isOffline"

    fun customPreference(context: Context): SharedPreferences = context.getSharedPreferences("AOO_SHARED", Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.isOffline
        get() = getBoolean(IS_OFFLINE, false)
        set(value) {
            editMe {
                it.putBoolean(IS_OFFLINE, value)
            }
        }


    var SharedPreferences.clearValues
        get() = run { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}