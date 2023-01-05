package com.azmiradi.churchapp.application_details

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.azmiradi.churchapp.FirebaseConstants.CLASSES
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


data class DetailsData(
    val applicationPojo: ApplicationPojo?, val zone: List<Zone>, val classes: List<Classes>
)

@HiltViewModel
class ApplicationDetailsViewModel @Inject constructor(val application: Application) : ViewModel() {
    private val _stateApplicationDetails = mutableStateOf(DataState<DetailsData>())
    val stateApplicationDetails: State<DataState<DetailsData>> = _stateApplicationDetails

    private val _stateUpdateApplication = mutableStateOf(DataState<Boolean>())
    val stateUpdateApplication: State<DataState<Boolean>> = _stateUpdateApplication


    private val _stateSendMail = mutableStateOf(DataState<Boolean>())
    val stateSendMail: State<DataState<Boolean>> = _stateSendMail

    val appDatabase = AppDatabase.getDatabase(application).applicationDao()

    val zonseDatabase = AppDatabase.getDatabase(application).zoneDao()

    private fun isOffline() = PreferenceHelper.customPreference(application).isOffline

    fun getApplicationDetails(nationalID: String) {
        _stateApplicationDetails.value = DataState(isLoading = true)
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object :
            ValueEventListener {
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
                        zoneList.add(it)
                    }
                }

                _stateApplicationDetails.value =
                    DataState(data = DetailsData(application, zoneList, classesList))
            }

            override fun onCancelled(error: DatabaseError) {
                _stateApplicationDetails.value = DataState(error = error.message)
            }

        })
    }


    fun updateApplication(applicationPojo: ApplicationPojo) {
        if (isOffline()) {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUpdateApplication.value = DataState(isLoading = true)
                appDatabase.updateApplications(applicationPojo)
                _stateUpdateApplication.value = DataState(data = true)
            }
        } else {
            _stateUpdateApplication.value = DataState(isLoading = true)
            FirebaseDatabase.getInstance().getReference(APPLICATIONS)
                .child(applicationPojo.nationalID).setValue(applicationPojo)
                .addOnSuccessListener {
                    _stateUpdateApplication.value = DataState(data = true)
                }.addOnFailureListener {
                    _stateUpdateApplication.value = DataState(error = it.message.toString())
                }
        }

    }

    fun sendMail(mailTo: String, file: File? = null, name: String? = "") {
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
                            "christmas.copticchurch@gmail.com", "ctcdtqvmxpekarac"
                        )
                    }
                })
                session.setDebug(true);
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(from))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(mailTo))
                message.subject =
                    "الكنيسة القبطية الارثوذكسية - دعوة حضور قداس عيد الميلاد المجيد 2023"
                val multipart: Multipart = MimeMultipart()

                if (file == null) {
                    //val attachmentPart = MimeBodyPart()
                    val textPart = MimeBodyPart()

                    try {
                        //  attachmentPart.attachFile(file)
                        textPart.setText(
                            "بكل التقدير نشكر سيادتكم لطلب دعوة حضور قداس عيد الميلاد المجيد ٢٠٢٣. " + "\n" + " ونحيط سيادتكم علماً بانه تم استلام بياناتكم وجاري العمل علي إصدار الدعوة الخاصه بكم" + "\n" + " سيتم التواصل مع سيادتكم بعد يوم ١ يناير ٢٠٢٣ من خلال رقم واتساب 01206019170 بشأن استلام الدعوة" + "\n" + "كل عام وحضراتكم بخير" + "\n" + "لجنة الدعوات.",
                            StandardCharsets.UTF_8.name()
                        )

                        multipart.addBodyPart(textPart)
                        //multipart.addBodyPart(attachmentPart)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        println("Email : " + mailTo + " : " + e.message)

                    }
                } else {
                    val attachmentPart = MimeBodyPart()
                    val textPart = MimeBodyPart()

                    try {
                        attachmentPart.attachFile(file)
                        val message = "كل عام وانتم بخير\n" +
                                " \n" +
                                "مرحباً بكم في قداس عيد الميلاد المجيد بكاتدرائية ميلاد المسيح بالعاصمة الإدارية الجديدة\n" +
                                "مرفق لسيادتكم الدعوة الإلكترونية الخاصة بمعالي السفير" + name +" نرجو من سيادتكم التواصل معنا عبر رقم  ٠١٢٠٦٠١٩١٧٠ لتحديد موعد استلام الدعوة الورقية من الكاتدرائية بالعباسية اليوم الموفق ٤ / ١ / ٢٠٢٣ من الساعة السابعة حتي العاشرة مساء علماً بأنه لن يمكن حضور قداس العيد بالكاتدرائية بالعاصمة الإدارية الجديدة بدون الدعوة الورقية ولن يعتد بالدعوة الإلكترونية. \n" +
                                "\n" +
                                "ملحوظه هامة هذه الدعوة الإلكترونية للاخطار فقط ولن تستخدم لحضوركم القداس بالكاتدرائية يوم ٦ يناير  ٢٠٢٣"
//                        val message =
//                            "كل عام وانتم بخير" + "\n" + "مرحباً بكم في قداس عيد الميلاد المجيد بكاتدرائية ميلاد المسيح بالعاصمة الادارية الجديده" + "\n" + " ضرورة تقديم الدعوة الورقية مع الدعوة الالكترونية عند الدخول ولن يعتد باحدهما دون الاخري" + "\n" + "عيد سعيد"
                        textPart.setText(
                            message, StandardCharsets.UTF_8.name()
                        )

                        multipart.addBodyPart(textPart)
                        multipart.addBodyPart(attachmentPart)
                    } catch (e: IOException) {
                        e.printStackTrace()

                    }
                }

                message.setContent(multipart)
                Transport.send(message)
                _stateSendMail.value = DataState(data = true)

            } catch (e: Throwable) {

                _stateSendMail.value = DataState(error = e.message ?: "Something worrying")
            }

        }

    }


    fun getApplicationDetailsByInvitation(invitationNumber: String) {
        _stateApplicationDetails.value = DataState(isLoading = true)
        if (isOffline()) {
            appDatabase.getApplication(invitationNumber).onEach {
                if (it.isNotEmpty()) {
                    _stateApplicationDetails.value = DataState(
                        data = DetailsData(
                            applicationPojo = it.last(), ArrayList(), ArrayList()
                        )
                    )
                } else {
                    _stateApplicationDetails.value = DataState(error = "رقم الدعوة غير صحيح")
                }
            }.launchIn(viewModelScope)
        } else {
            FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var applicationPojo: ApplicationPojo? = null
                    for (data in snapshot.child(APPLICATIONS).children) {
                        if (data.getValue(ApplicationPojo::class.java)?.invitationNumber == invitationNumber) {
                            applicationPojo = data.getValue(ApplicationPojo::class.java)
                        }
                    }

                    val zoneList: MutableList<Zone> = ArrayList()
                    val classesList: MutableList<Classes> = ArrayList()

                    for (data in snapshot.child(CLASSES).children) {
                        data.getValue(Classes::class.java)?.let {
                            classesList.add(it)
                        }
                    }

                    for (data in snapshot.child(ZONE).children) {
                        data.getValue(Zone::class.java)?.let {
                            zoneList.add(it)
                        }
                    }

                    if (applicationPojo != null) {
                        _stateApplicationDetails.value = DataState(
                            data = DetailsData(
                                applicationPojo, zoneList, classesList
                            )
                        )
                    } else {
                        _stateApplicationDetails.value =
                            DataState(error = "رقم الدعوة غير صحيح")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateApplicationDetails.value = DataState(error = error.message)
                }

            })
        }
    }

    fun resetViewModel() {
        _stateUpdateApplication.value = DataState()
        _stateSendMail.value = DataState()
        _stateApplicationDetails.value = DataState()
    }


}