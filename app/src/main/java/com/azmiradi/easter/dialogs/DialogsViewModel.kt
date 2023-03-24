package com.azmiradi.easter.dialogs

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azmiradi.easter.DataState
import com.azmiradi.easter.FirebaseConstants
import com.azmiradi.easter.local_database.AppDatabase
import com.azmiradi.easter.local_database.Zone
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogsViewModel @Inject constructor(val application: Application) : ViewModel() {
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

    fun <T> addList(t: T, root: String) {
        _addState.value = DataState(isLoading = true)
        val database = FirebaseDatabase.getInstance().reference.child(root)
        database.setValue(t).addOnCompleteListener {
            _addState.value = DataState(data = true)
        }.addOnFailureListener {
            _addState.value = DataState(error = it.message.toString())
        }
    }


    private val database=AppDatabase.getDatabase(application).zoneDao()
    fun addLocalZone(zone: Zone){
        viewModelScope.launch {
            database.addZone(zone)
        }
    }
    private val _stateZones = mutableStateOf(DataState<List<Zone>>())
    val stateZones: State<DataState<List<Zone>>> = _stateZones

    fun getAllZones() {
        _stateZones.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().reference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val zoneList: MutableList<Zone> = ArrayList()

                    for (data in snapshot.child(FirebaseConstants.ZONE).children) {
                        data.getValue(Zone::class.java)?.let {
                             zoneList.add(it)
                        }
                    }
                    _stateZones.value = DataState(data = zoneList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateZones.value = DataState(error = error.message)
                }

            })
    }

    fun resetView() {
        _addState.value = DataState()
        _stateZones.value = DataState()
    }


}