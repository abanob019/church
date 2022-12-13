package com.azmiradi.churchapp.all_applications

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllApplicationViewModel @Inject constructor() : ViewModel() {
    private val _stateApplications = mutableStateOf(DataState<List<ApplicationPojo>>())
    val stateApplications: State<DataState<List<ApplicationPojo>>> = _stateApplications

    fun getApplications() {
        _stateApplications.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().reference.child(APPLICATIONS).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList: MutableList<ApplicationPojo> = ArrayList()
                    for (dataSnap in snapshot.children) {
                        dataSnap.getValue(ApplicationPojo::class.java)?.let {
                            dataList.add(it)
                        }
                    }
                    _stateApplications.value = DataState(data = dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateApplications.value = DataState(error = error.message)
                }

            }
        )
    }



}