package com.azmiradi.invitations

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.azmiradi.invitations.login.LoginViewModel
import javax.inject.Inject


class MyPreferences @Inject constructor(application: Application) {

    companion object {
        private const val PREFS_NAME = "my_prefs"
        private const val IS_LOGIN = "isLogin"
        private const val RULE = "rule"

    }

    private var preferences: SharedPreferences

    init {
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN, false)
        set(value) = preferences.edit().putBoolean(IS_LOGIN, value).apply()


    var ruel: LoginViewModel.Rule
        get() = LoginViewModel.Rule.valueOf(
            preferences.getString(
                RULE,
                LoginViewModel.Rule.READ_B.name
            ) ?: LoginViewModel.Rule.READ_B.name
        )
        set(value) = preferences.edit().putString(RULE, value.name).apply()


}
