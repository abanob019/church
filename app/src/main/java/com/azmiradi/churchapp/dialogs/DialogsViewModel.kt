package com.azmiradi.churchapp.dialogs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azmiradi.churchapp.DataState
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogsViewModel @Inject constructor() : ViewModel() {
    private val _addState = mutableStateOf(DataState<Boolean>())
    val addState: State<DataState<Boolean>> = _addState

    fun <T> addData(t: T, root: String) {
        _addState.value = DataState(isLoading = true)
        val database = FirebaseDatabase.getInstance().reference.child(root)
        database.push().setValue(t).addOnCompleteListener {
            _addState.value = DataState(data = true)
        }.addOnFailureListener {
            _addState.value = DataState(error = it.message.toString())
        }
    }

    fun resetView(){
        _addState.value = DataState()
    }


}