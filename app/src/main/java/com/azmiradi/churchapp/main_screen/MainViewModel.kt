package com.azmiradi.churchapp.main_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azmiradi.churchapp.DataState
import com.azmiradi.churchapp.FirebaseConstants.APPLICATIONS
import com.azmiradi.churchapp.FirebaseConstants.CLASSES
import com.azmiradi.churchapp.FirebaseConstants.ZONE
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.azmiradi.churchapp.application_details.Classes
import com.azmiradi.churchapp.application_details.DetailsData
import com.azmiradi.churchapp.application_details.Zone
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

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _stateSendMail = mutableStateOf(DataState<Boolean>())
    val stateSendMail: State<DataState<Boolean>> = _stateSendMail

    fun sendMail(information: String, mailTo: String) {
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


}