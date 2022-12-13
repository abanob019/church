package com.azmiradi.churchapp.application_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.azmiradi.churchapp.FirebaseConstants.CLASSES
import com.azmiradi.churchapp.FirebaseConstants.ZONE
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class DetailsData(
    val applicationPojo: ApplicationPojo?,
    val zone: List<Zone>,
    val classes: List<Classes>
)

@HiltViewModel
class ApplicationDetailsViewModel @Inject constructor() : ViewModel() {
    private val _stateApplicationDetails = mutableStateOf(DataState<DetailsData>())
    val stateApplicationDetails: State<DataState<DetailsData>> = _stateApplicationDetails

    private val _stateUpdateApplication = mutableStateOf(DataState<Boolean>())
    val stateUpdateApplication: State<DataState<Boolean>> = _stateUpdateApplication


    fun getApplicationDetails(nationalID: String) {
        _stateApplicationDetails.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().reference
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val application = snapshot.child(APPLICATIONS).child(nationalID)
                            .getValue(ApplicationPojo::class.java)
                        val zoneList: MutableList<Zone> = ArrayList()
                        val classesList: MutableList<Classes> = ArrayList()

                        for (data in snapshot.child(CLASSES).children) {
                            data.getValue(Classes::class.java)?.let {
                                classesList.add(it)
                            }
                        }

                        for (data in snapshot.child(ZONE).children) {
                            data.getValue(Zone::class.java)?.let {
                                it.zoneID= data.key
                                zoneList.add(it)
                            }
                        }

                        _stateApplicationDetails.value =
                            DataState(data = DetailsData(application, zoneList, classesList))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _stateApplicationDetails.value = DataState(error = error.message)
                    }

                }
            )
    }

    fun updateApplication(applicationPojo: ApplicationPojo) {
        _stateUpdateApplication.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().getReference(APPLICATIONS)
            .child(applicationPojo.nationalID.toString())
            .setValue(applicationPojo).addOnSuccessListener {
                _stateUpdateApplication.value = DataState(data = true)
            }.addOnFailureListener {
                _stateUpdateApplication.value = DataState(error = it.message.toString())
            }
    }


}