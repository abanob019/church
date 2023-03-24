package com.azmiradi.easter.attendence

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azmiradi.easter.DataState
import com.azmiradi.easter.FirebaseConstants.APPLICATIONS
import com.azmiradi.easter.all_applications.ApplicationPojo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendedViewModel @Inject constructor() :
    ViewModel() {
    private val _stateApplications = mutableStateOf(DataState<List<ApplicationPojo>>())
    val stateApplications: State<DataState<List<ApplicationPojo>>> = _stateApplications


    fun getApplications() {
        _stateApplications.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().getReference(APPLICATIONS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList: MutableList<ApplicationPojo> = ArrayList()
                for (dataSnap in snapshot.children) {
                    dataSnap.getValue(ApplicationPojo::class.java)?.let {
                        dataList.add(it)
                    }
                }
                val filteredData = dataList.filter {
                    it.isAttend == true
                }.sortedByDescending {
                    it.attendDate
                }
                _stateApplications.value = DataState(data = filteredData)
            }

            override fun onCancelled(error: DatabaseError) {
                _stateApplications.value = DataState(error = error.message)
            }

        })
    }
}