package com.azmiradi.churchapp.all_applications

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.azmiradi.churchapp.application_details.Classes
import com.azmiradi.churchapp.application_details.Zone
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

    private val _stateZones = mutableStateOf(DataState<List<Zone>>())
    val stateZones: State<DataState<List<Zone>>> = _stateZones

    private val _stateClasses = mutableStateOf(DataState<List<Classes>>())
    val stateClasses: State<DataState<List<Classes>>> = _stateClasses

    var allApplications: List<ApplicationPojo> = ArrayList()

    fun getApplications() {
        _stateApplications.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().reference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList: MutableList<ApplicationPojo> = ArrayList()
                    val zoneList: MutableList<Zone> = ArrayList()
                    val classesList: MutableList<Classes> = ArrayList()

                    for (dataSnap in snapshot.child(APPLICATIONS).children) {
                        dataSnap.getValue(ApplicationPojo::class.java)?.let {
                            dataList.add(it)
                        }
                    }

                    for (data in snapshot.child(FirebaseConstants.CLASSES).children) {
                        data.getValue(Classes::class.java)?.let {
                            classesList.add(it)
                        }
                    }

                    for (data in snapshot.child(FirebaseConstants.ZONE).children) {
                        data.getValue(Zone::class.java)?.let {
                             zoneList.add(it)
                        }
                    }

                    allApplications = dataList
                    _stateApplications.value = DataState(data = dataList)
                    _stateClasses.value = DataState(data = classesList)
                    _stateZones.value = DataState(data = zoneList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateApplications.value = DataState(error = error.message)
                }

            })
    }

    fun getApplications(type: ApplicationsType) {
        when (type) {
            ApplicationsType.All -> {
                _stateApplications.value = DataState(data = allApplications)
            }
            ApplicationsType.Active -> {
                val data = allApplications.filter {
                    it.isApproved == true
                }
                _stateApplications.value = DataState(data = data)
            }
            ApplicationsType.DisActive -> {
                val data = allApplications.filter {
                    it.isApproved != true
                }
                _stateApplications.value = DataState(data = data)
            }

            ApplicationsType.Attended -> {
                val data = allApplications.filter {
                    it.isAttend == true
                }
                _stateApplications.value = DataState(data = data)
            }
        }
    }

    private val _stateUpdateData = mutableStateOf(DataState<Boolean>())
    val stateUpdateData: State<DataState<Boolean>> = _stateUpdateData
    fun updateData(
        toList: List<ApplicationPojo>, index: Int
    ) {
        val ref = FirebaseDatabase.getInstance().reference.child(APPLICATIONS)
        ref.child(toList[index].nationalID.toString()).setValue(toList[index])
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (index == (toList.size - 1)) {
                         _stateUpdateData.value = DataState(data = true)
                    } else {
                        updateData(toList, index + 1)
                    }
                } else {
                    _stateUpdateData.value = DataState(error = task.exception?.message.toString())
                }
            }.addOnFailureListener {
                _stateUpdateData.value = DataState(error = it.message.toString())
            }
    }

}