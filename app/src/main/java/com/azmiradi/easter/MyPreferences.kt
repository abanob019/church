package com.azmiradi.easter

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject


class MyPreferences @Inject constructor(application: Application) {

    companion object {
        private const val PREFS_NAME = "my_prefs"
        private const val IS_LOGIN = "isLogin"
        private const val IS_ADMIN = "isAdmin"
        private const val USER_NAME = "userName"

    }

    private var preferences: SharedPreferences

    init {
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN, false)
        set(value) = preferences.edit().putBoolean(IS_LOGIN, value).apply()

    var isAdmin: Boolean
        get() = preferences.getBoolean(IS_ADMIN, false)
        set(value) = preferences.edit().putBoolean(IS_ADMIN, value).apply()

    var userName: String
        get() = preferences.getString(USER_NAME, "") ?: ""
        set(value) = preferences.edit().putString(USER_NAME, value).apply()




}
