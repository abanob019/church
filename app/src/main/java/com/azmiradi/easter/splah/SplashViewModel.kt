package com.azmiradi.easter.splah

import androidx.lifecycle.ViewModel
import com.azmiradi.easter.MyPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val myPreferences: MyPreferences) : ViewModel() {
    fun isLogin() = myPreferences.isLogin

    fun logout() {
        myPreferences.isLogin = false
        myPreferences.isAdmin = false
    }

}