package com.azmiradi.churchapp.main_screen

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.azmiradi.churchapp.FirebaseConstants.ZONE
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.azmiradi.churchapp.local_database.AppDatabase
import com.azmiradi.churchapp.local_database.PreferenceHelper
import com.azmiradi.churchapp.local_database.PreferenceHelper.isOffline
import com.azmiradi.churchapp.local_database.Zone
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

@HiltViewModel
class MainViewModel @Inject constructor(val application: Application) : ViewModel() {
    private val _stateSendMail = mutableStateOf(DataState<Boolean>())
    val stateSendMail: State<DataState<Boolean>> = _stateSendMail
    var job: Job? = null

    val applicationsDB = AppDatabase.getDatabase(application).applicationDao()

    init {
        if (!PreferenceHelper.customPreference(application).isOffline)
            getApplications()
    }

    private val zonesDB = AppDatabase.getDatabase(application).zoneDao()


    var uploadSavedDataJob: Job? = null

    fun isOffline() = PreferenceHelper.customPreference(application).isOffline
    fun setIsOffline(isOffline: Boolean) {
        if (!isOffline) {
            uploadSavedDataJob = viewModelScope.launch(Dispatchers.IO) {
                job = applicationsDB.getApplications()
                    .onEach {
                        val data = it.filter { obj ->
                            obj.isAttend == true
                        }
                        if (data.isNotEmpty()) {
                            _stateUpdateData.value = DataState(isLoading = true)
                            updateData(data, 0)
                        }
                    }.launchIn(viewModelScope)
            }
        }
        PreferenceHelper.customPreference(application).isOffline = isOffline
    }

    fun sendMail(information: String, mailTo: String) {
        _stateSendMail.value = DataState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val from = "christmas.copticchurch@gmail.com"
                val host = "smtp.gmail.com"
                val properties = System.getProperties()
                properties["mail.smtp.host"] = host
                properties["mail.smtp.port"] = "465"
                properties["mail.smtp.ssl.enable"] = "true"
                properties["mail.smtp.auth"] = "true"
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(
                            "christmas.copticchurch@gmail.com",
                            "ctcdtqvmxpekarac"
                        )
                    }
                })
                //session.setDebug(true);
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(from))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(mailTo))
                message.subject = "الكنيسة القبطية الارثوذكسية - معلومات جلوسك في الكنيسة"
                val multipart: Multipart = MimeMultipart()
                //  val attachmentPart = MimeBodyPart()
                val textPart = MimeBodyPart()

                try {
                    //attachmentPart.attachFile(file)
                    textPart.setText(
                        "مرحباً بك في كاتدرائية ميلاد المسيح " +
                                "\n" +
                                "اليكم معلومات جلوسكم في الكنيسة" +
                                "\n" +
                                "\n" +
                                information +
                                "\n" +
                                "\n" +
                                "كل سنه وانتم بكل خير " +
                                "\n" +
                                "عيد الميلاد المجيد 2023", StandardCharsets.UTF_8.name()
                    )

                    multipart.addBodyPart(textPart)
                    //multipart.addBodyPart(attachmentPart)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                message.setContent(multipart)
                Transport.send(message)
                _stateSendMail.value = DataState(data = true)

            } catch (e: Throwable) {
                _stateSendMail.value = DataState(error = e.message ?: "Something worrying")
            }

        }

    }

    private fun getApplications() {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList: MutableList<ApplicationPojo> = ArrayList()

                    for (dataSnap in snapshot.child(APPLICATIONS).children) {
                        dataSnap.getValue(ApplicationPojo::class.java)?.let {
                            dataList.add(it)
                        }
                    }

                    val zonesList: MutableList<Zone> = ArrayList()

                    for (dataSnap in snapshot.child(ZONE).children) {
                        dataSnap.getValue(Zone::class.java)?.let {
                            zonesList.add(it)
                        }
                    }

                    viewModelScope.launch(Dispatchers.IO) {
                        zonesDB.deleteZones()
                        applicationsDB.deleteApplications()
                        zonesDB.addZones(zonesList)
                        applicationsDB.addApplications(dataList.map {
                            it.copy(isAttend = false)
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
    }

    private val _stateUpdateData = mutableStateOf(DataState<Boolean>())
    val stateUpdateData: State<DataState<Boolean>> = _stateUpdateData
    private fun updateData(
        toList: List<ApplicationPojo>, index: Int
    ) {
        val ref = FirebaseDatabase.getInstance().reference.child(APPLICATIONS)
        ref.child(toList[index].nationalID).setValue(toList[index])
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (index == (toList.size - 1)) {
                        job?.cancel()
                        uploadSavedDataJob?.cancel()
                        getApplications()
                        _stateUpdateData.value = DataState(data = true)
                    } else {
                        updateData(toList, index + 1)
                    }
                } else {
                    uploadSavedDataJob?.cancel()
                    job?.cancel()
                    _stateUpdateData.value = DataState(error = task.exception?.message.toString())
                }
            }.addOnFailureListener {
                job?.cancel()
                uploadSavedDataJob?.cancel()
                _stateUpdateData.value = DataState(error = it.message.toString())
            }
    }


    private fun <T> addList(t: T, root: String) {
        val database = FirebaseDatabase.getInstance().reference.child(root)
        database.setValue(t).addOnCompleteListener {
            job?.cancel()
        }.addOnFailureListener {
        }
    }

}