package com.azmiradi.invitations

import androidx.lifecycle.ViewModel



abstract class BaseViewModel : ViewModel() {
    abstract fun resetState()
    abstract fun isLoading():Boolean
    abstract fun toastMessage():String
}
