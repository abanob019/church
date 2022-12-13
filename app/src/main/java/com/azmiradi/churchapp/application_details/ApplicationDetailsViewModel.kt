package com.azmiradi.churchapp.application_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Dispatchers
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


    private val _stateSendMail = mutableStateOf(DataState<Boolean>())
    val stateSendMail: State<DataState<Boolean>> = _stateSendMail


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
                                it.zoneID = data.key
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

    fun sendMail(mailTo: String, file: File) {
        _stateSendMail.value = DataState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val from = "azmiradi97@gmail.com"
                val host = "smtp.gmail.com"
                val properties = System.getProperties()
                properties["mail.smtp.host"] = host
                properties["mail.smtp.port"] = "465"
                properties["mail.smtp.ssl.enable"] = "true"
                properties["mail.smtp.auth"] = "true"
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("azmiradi97@gmail.com", "hgqmsfzyooefobla")
                    }
                })
                //session.setDebug(true);
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(from))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(mailTo))
                message.subject = "الكنيسة القبطية الارثوذكسية - دعوة قداس عيد الميلا المجيد 2023"
                val multipart: Multipart = MimeMultipart()
                val attachmentPart = MimeBodyPart()
                val textPart = MimeBodyPart()

                try {
                    attachmentPart.attachFile(file)
                    textPart.setText(
                        " نبلغ سيادتكم بانه تم تفعيل الدعوه الخاصه بكم \n" +
                                "مرفق لكم صور من الـ 'QRCode' الخاص بدعوة سيادتكم برجاء تقديمه لمن يطلب اثناء الدخول لمساعدتكم \n " +
                                "كل سنه وانتم بكل خير " +
                                "\n" +
                                "عيد الميلاد المجيد 2023", StandardCharsets.UTF_8.name()
                    )

                    multipart.addBodyPart(textPart)
                    multipart.addBodyPart(attachmentPart)
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


}