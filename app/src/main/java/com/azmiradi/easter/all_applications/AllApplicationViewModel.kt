package com.azmiradi.easter.all_applications

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azmiradi.easter.DataState
import com.azmiradi.easter.FirebaseConstants
import com.azmiradi.easter.FirebaseConstants.APPLICATIONS
import com.azmiradi.easter.application_details.Classes
import com.azmiradi.easter.local_database.Zone
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllApplicationViewModel @Inject constructor() :
    ViewModel() {
    private val _stateApplications = mutableStateOf(DataState<List<ApplicationPojo>>())
    val stateApplications: State<DataState<List<ApplicationPojo>>> = _stateApplications

    private val _stateZones = mutableStateOf(DataState<List<Zone>>())
    val stateZones: State<DataState<List<Zone>>> = _stateZones

    private val _stateClasses = mutableStateOf(DataState<List<Classes>>())
    val stateClasses: State<DataState<List<Classes>>> = _stateClasses
    var allApplications = mutableStateListOf<ApplicationPojo>()

    fun getApplications() {
        _stateApplications.value = DataState(isLoading = true)

        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList: MutableList<ApplicationPojo> = ArrayList()
                val zoneList: MutableList<Zone> = ArrayList()
                val classesList: MutableList<Classes> = ArrayList()

                for (dataSnap in snapshot.child(APPLICATIONS).children) {
                    dataSnap.getValue(ApplicationPojo::class.java)?.let {
                        dataList.add(it)
                    }
                }

                viewModelScope.launch(Dispatchers.IO) {
                    //  applicationDao.addListApplication(dataList)
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
                println("Size Of Data:-> "+ dataList.size)
                allApplications.clear()
                allApplications.addAll(dataList)
                _stateApplications.value = DataState(data = dataList)
                _stateClasses.value = DataState(data = classesList)
                _stateZones.value = DataState(data = zoneList)
            }

            override fun onCancelled(error: DatabaseError) {
                _stateApplications.value = DataState(error = error.message)
            }

        })
    }

    fun getApplications(type: ApplicationsType, keyWord: String = "") {
        if (allApplications.isNotEmpty())
        {
            val data = when (type) {
                ApplicationsType.All -> {
                    allApplications
                }
                ApplicationsType.Active -> {
                    allApplications.filter {
                        it.isApproved == true
                    }
                }
                ApplicationsType.DisActive -> {
                    allApplications.filter {
                        it.isApproved != true
                    }
                }

                ApplicationsType.Attended -> {
                    allApplications.filter {
                        it.isAttend == true
                    }.sortedByDescending {
                        it.attendDate
                    }
                }
            }
            if (keyWord.isEmpty()|| keyWord.isBlank())
                _stateApplications.value = DataState(data = data)
            else
                _stateApplications.value = DataState(data = data.filter {
                    it.name?.contains(keyWord) == true
                            || it.title?.contains(keyWord) == true
                            || it.jobTitle?.contains(keyWord) == true
                            || it.employer?.contains(keyWord) == true
                })
        }
    }

    private val _stateUpdateData = mutableStateOf(DataState<Boolean>())
    val stateUpdateData: State<DataState<Boolean>> = _stateUpdateData

    fun updateData(
        toList: List<ApplicationPojo>, index: Int
    ) {
        if (index == 0)
            _stateUpdateData.value = DataState(isLoading = true)
        val ref = FirebaseDatabase.getInstance().reference.child(APPLICATIONS)
        ref.child(toList[index].nationalID).setValue(toList[index])
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

    fun resetData() {
        _stateApplications.value = DataState()
        _stateClasses.value = DataState()
        _stateZones.value = DataState()
        _stateUpdateData.value = DataState()
    }

}