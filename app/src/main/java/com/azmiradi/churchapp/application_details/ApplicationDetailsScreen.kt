package com.azmiradi.churchapp.application_details

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.RealPathUtil
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.azmiradi.churchapp.local_database.Zone
import com.izettle.html2bitmap.Html2Bitmap
import com.izettle.html2bitmap.content.WebViewContent.html
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream


@Composable
fun ApplicationDetailsScreen(
    applicationID: String,
    onNavigation: (destination: String) -> Unit,
    viewModel: ApplicationDetailsViewModel = hiltViewModel()
) {
    val listOfEmployer = remember {
        listOf(
            "مجلس النواب",
            "مجلس الشيوخ",
            "هيئات قضائية",
            "بعثات دبلوماسية",
            "جامعات",
            "مجالس قومية",
            "هيئات حكومية",
            "بنوك وهيئات اقتصادية",
            "صحافة واعلام",
            "نقابات",
            "احزاب وائتلافات",
            "جمعيات اهلية",
            "رجال اعمال",
            "اخري"
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getApplicationDetails(applicationID)
    }

    val state = viewModel.stateApplicationDetails.value
    val sendMailState = viewModel.stateSendMail.value
    val updateState = viewModel.stateUpdateApplication.value
    val context = LocalContext.current

    ProgressBar(
        isShow = state.isLoading || updateState.isLoading || sendMailState.isLoading

    )

    if (state.error.isNotEmpty() || updateState.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }

    var applicationPojo by remember {
        mutableStateOf<ApplicationPojo?>(null)
    }
    val zones = remember {
        mutableListOf<Zone>()
    }
    val classes = remember {
        mutableListOf<Classes>()
    }
    var data by remember {
        mutableStateOf<DetailsData?>(null)
    }

    val selectedZone = remember {
        mutableStateOf(0)
    }

    val selectedClass = remember {
        mutableStateOf(0)
    }

    val note = remember {
        mutableStateOf("")
    }

    val char = remember {
        mutableStateOf("")
    }

    val priority = remember {
        mutableStateOf("0")
    }
    val row = remember {
        mutableStateOf("")
    }

    val invitationBitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val title = remember {
        mutableStateOf("")
    }
    val name = remember {
        mutableStateOf("")
    }
    val jobTitle = remember {
        mutableStateOf("")
    }
    val nationalID = remember {
        mutableStateOf("")
    }
    val employer = remember {
        mutableStateOf(0)
    }
    val phone = remember {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    state.data?.let {
        LaunchedEffect(Unit) {
            data = it
            applicationPojo = data?.applicationPojo

            zones.clear()
            data?.zone?.let { it1 -> zones.addAll(it1) }

            classes.clear()
            data?.classes?.let { it1 -> classes.addAll(it1) }

            data?.classes?.forEachIndexed { index, data ->
                if (data.className == applicationPojo?.className) {
                    selectedClass.value = index
                }
            }

            data?.zone?.forEachIndexed { index, data ->
                if (data.zoneName == applicationPojo?.zoneID) {
                    selectedZone.value = index
                }
            }

            note.value = applicationPojo?.note ?: ""

            row.value = applicationPojo?.row ?: ""

            char.value = applicationPojo?.chare ?: ""
            priority.value = (applicationPojo?.priority ?: 0).toString()

            title.value = applicationPojo?.title ?: ""
            name.value = applicationPojo?.name ?: ""
            jobTitle.value = applicationPojo?.jobTitle ?: ""
            nationalID.value = applicationPojo?.nationalID ?: ""
            phone.value = applicationPojo?.phone ?: ""

            applicationPojo?.employer?.let {
                println("jobTitle: : " + it)
                employer.value = listOfEmployer.indexOf(it)
            }

            if (applicationPojo?.isApproved == true) {
                coroutineScope.launch(Dispatchers.IO) {
                    val invitation = context.prepareQRWWithInvitation(
                        applicationPojo,
                        zones.getOrNull(selectedZone.value)
                    ) {
                        coroutineScope.launch(Dispatchers.Main) {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    }
                    invitationBitmap.value = invitation
                }
            }
        }
    }


    updateState.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "تم تحديث البيانات", Toast.LENGTH_LONG).show()
            invitationBitmap.value = null
            viewModel.resetViewModel()
            viewModel.getApplicationDetails(nationalID = applicationID)
//            coroutineScope.launch(Dispatchers.IO) {
//                val invitation = context.prepareQRWWithInvitation(
//                    applicationPojo,
//                    zones.getOrNull(selectedZone.value)
//                )
//                invitationBitmap.value = invitation
//            }
        }
    }

    sendMailState.data?.let {
        LaunchedEffect(Unit) {
            applicationPojo?.isSandedApproved = true
            applicationPojo?.let { it1 -> viewModel.updateApplication(it1) }
            Toast.makeText(context, "تم ارسال الدعوه علي الايميل", Toast.LENGTH_LONG).show()
        }
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextFile(data = title, title = "اللقب", keyboard = KeyboardType.Text)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextFile(data = name, title = "الاسم", keyboard = KeyboardType.Text)
            Spacer(modifier = Modifier.height(10.dp))


            CustomTextFile(data = jobTitle, title = "المسمي الوظيفي", keyboard = KeyboardType.Text)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextFile(data = nationalID, title = "الرقم القومي", keyboard = KeyboardType.Text)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextFile(data = phone, title = "رقم الهاتف", keyboard = KeyboardType.Text)
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner(
                "الجهة", list = listOfEmployer, employer.value
            ) {
                employer.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = priority, title = "الاولوية", keyboard = KeyboardType.Decimal)
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner(
                "نوع الدعوة", list = classes.mapNotNull {
                    it.className
                }, selectedClass.value
            ) {
                selectedClass.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))


            SampleSpinner(
                "المنطقه", list = zones.mapNotNull { it.zoneName }, selectedZone.value
            ) {
                selectedZone.value = it
            }

            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = row, title = "الصف")

            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = char, title = "الكرسي")

            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = note, title = "كتابة ملاحظات", height = 150, isSingleLine = false)

            if (applicationPojo?.isApproved == true) {
                Spacer(modifier = Modifier.height(10.dp))

                invitationBitmap.value?.asImageBitmap()?.let {
                    Image(bitmap = it, contentDescription = "")
                }

                Row(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                        onClick = {
                            applicationPojo?.zoneID = zones.getOrNull(selectedZone.value)?.zoneName
                            applicationPojo?.isApproved = true
                            applicationPojo?.note = note.value
                            applicationPojo?.className =
                                classes.getOrNull(selectedClass.value)?.className
                            applicationPojo?.priority = priority.value.trim().toIntOrNull() ?: 0
                            applicationPojo?.title = title.value
                            applicationPojo?.name = name.value
                            applicationPojo?.nationalID = nationalID.value
                            applicationPojo?.jobTitle = jobTitle.value
                            applicationPojo?.phone = phone.value
                            applicationPojo?.employer = listOfEmployer.getOrNull(employer.value)
                            applicationPojo?.chare = char.value
                            applicationPojo?.row = row.value
                            applicationPojo?.let { viewModel.updateApplication(it) }
                        }) {
                        Text(
                            text = "تحديث البيانات", fontSize = 16.sp
                        )
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                        onClick = {
                            applicationPojo?.zoneID = zones.getOrNull(selectedZone.value)?.zoneName
                            applicationPojo?.isApproved = false
                            applicationPojo?.note = note.value
                            applicationPojo?.className =
                                classes.getOrNull(selectedClass.value)?.className
                            applicationPojo?.priority = priority.value.trim().toIntOrNull() ?: 0
                            applicationPojo?.title = title.value
                            applicationPojo?.name = name.value
                            applicationPojo?.nationalID = nationalID.value
                            applicationPojo?.jobTitle = jobTitle.value
                            applicationPojo?.phone = phone.value
                            applicationPojo?.employer = listOfEmployer.getOrNull(employer.value)
                            applicationPojo?.chare = char.value
                            applicationPojo?.row = row.value
                            applicationPojo?.let { viewModel.updateApplication(it) }
                        }) {
                        Text(
                            text = "الغاء تفعيل الدعوة", fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 10.dp),
                        onClick = {
                            val image = invitationBitmap.value?.saveBitmap(
                                "/Invitations/Invitations",
                                applicationPojo?.name + "_" + applicationPojo?.phone, context
                            ) {
                                Toast.makeText(
                                    context,
                                    "فشل حفظ الدعوه لارسالها",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            image?.let {
                                viewModel.sendMail(
                                    applicationPojo?.email ?: "",
                                    File(RealPathUtil.getRealPath(context, image).toString())
                                )
                            }

                        }) {
                        Text(text = "Email", fontSize = 16.sp)
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                        onClick = {
                            invitationBitmap.value?.let {
                                context.sendInvitationViaWhatsApp(
                                    it,
                                    applicationPojo?.phone.toString()
                                )
                            }
                        }) {
                        Text(
                            text = "Whatsapp", fontSize = 16.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    applicationPojo?.zoneID = zones.getOrNull(selectedZone.value)?.zoneName
                    applicationPojo?.isApproved = true
                    applicationPojo?.note = note.value
                    applicationPojo?.className = classes.getOrNull(selectedClass.value)?.className
                    applicationPojo?.priority = priority.value.trim().toIntOrNull() ?: 0
                    applicationPojo?.title = title.value
                    applicationPojo?.name = name.value
                    applicationPojo?.nationalID = nationalID.value
                    applicationPojo?.jobTitle = jobTitle.value
                    applicationPojo?.phone = phone.value
                    applicationPojo?.employer = listOfEmployer.getOrNull(employer.value)
                    applicationPojo?.chare = char.value
                    applicationPojo?.row = row.value
                    applicationPojo?.let { viewModel.updateApplication(it) }
                    viewModel.sendMail(applicationPojo?.email ?: "", null)
                }) {
                    Text(
                        text = "الموافق علي الدعوه", fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun Context.sendInvitationViaWhatsApp(imgUri: Bitmap, phone: String) {
    val path: String =
        MediaStore.Images.Media.insertImage(contentResolver, imgUri, "Image Description", null)
    val uri = Uri.parse(path)

    val whatsAppLink = "2$phone@s.whatsapp.net"
    val message = "كل عام وانتم بخير" +
            "\n" +
            "مرحباً بكم في قداس عيد الميلاد المجيد بكاتدرائية ميلاد المسيح بالعاصمة الادارية الجديده" +
            "\n" +
            " ضرورة تقديم الدعوة الورقية مع الدعوة الالكترونية عند الدخول ولن يعتد باحدهما دون الاخري" +
            "\n" +
            "عيد سعيد"
    val sendIntent = Intent(Intent.ACTION_SEND)
    sendIntent.type = "text/plain"
    sendIntent.putExtra(Intent.EXTRA_TEXT, message)
    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
    sendIntent.type = "image/jpeg"
    sendIntent.putExtra("jid", whatsAppLink)
    sendIntent.setPackage("com.whatsapp")
    startActivity(sendIntent)
}

fun Context.prepareQRWWithInvitation(
    applicationPojo: ApplicationPojo?,
    zone: Zone?,
    onError: (String) -> Unit
): Bitmap? {
    applicationPojo?.let {
        val qrBitmap = createQRCode(
            applicationPojo,
            zone = zone,
            withColor = true
        )
        val qrUri = qrBitmap.saveBitmap(
            folder = "/Invitations/ColorsQRs",
            (applicationPojo.name ?: "") + "_" + (applicationPojo.phone ?: ""), this
        ) {
            onError(it)
        }
        qrUri?.let {
            val invitationBitmap = prepareInvitation(
                (applicationPojo.title ?: " ") + " " + (applicationPojo.name ?: ""),
                it.toString()
            )
            invitationBitmap?.saveBitmap(
                "/Invitations/Invitations",
                (applicationPojo.name ?: "") + "_" + (applicationPojo.phone ?: ""), this
            ) { error ->
                onError(error)
            }
            return invitationBitmap
        }
    }
    return null
}

fun Context.prepareInvitation(applicationName: String, qrImage: String): Bitmap? {
    val inputStream: InputStream = assets.open("invitation.html")
    val size = inputStream.available()

    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()

    val html = String(buffer)
    val fullImage = html.replace("APPLICATION_NAME", applicationName)
        .replace("QR_IMAGE", qrImage)
    return Html2Bitmap.Builder().setContext(this).setBitmapWidth(1182).setContent(html(fullImage))
        .build().bitmap
}


fun createQRCode(
    applicationPojo: ApplicationPojo?,
    zone: Zone?,
    withColor: Boolean
): Bitmap {
    val data = java.lang.StringBuilder("عيد الميلاد  2023").append("\n")
        .append(applicationPojo?.title + " : " + applicationPojo?.name).append("\n")
        .append(applicationPojo?.nationalID).append("\n")
        .append(zone?.zoneName ?: "").append(" - ").append(applicationPojo?.row ?: "").append(" - ")
        .append(applicationPojo?.chare ?: "").append("\n")
        .append(applicationPojo?.employer)
        .append("\n").append(zone?.zoneColor ?: "")

//    val options = QrOptions.Builder(115)
//        .setColors(
//        QrColors(
//            dark = QrColor.Solid(getColor(R.color.black)),
//            highlighting = QrColor.Solid(0xddffffff.toColor()),
//        )
//    ).setElementsShapes(
//        QrElementsShapes(
//            darkPixel = QrPixelShape.RoundCorners(),
//            ball = QrBallShape.RoundCorners(.25f),
//            frame = QrFrameShape.RoundCorners(.25f),
//        )
//    ).setPadding(0f).build()
//
//    val generator = QrCodeGenerator(this)
//    return generator.generateQrCode(
//        QrData.Text(data.toString()), options, StandardCharsets.UTF_8
//    )

    // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
    // Initializing the QR Encoder with your value to be encoded, type you required and Dimension

    val qrgEncoder = QRGEncoder(data.toString(), null, QRGContents.Type.TEXT, 150)
//    if (withColor)
//        qrgEncoder.colorWhite = ColorsZ.values().find {
//            it.name == zone?.zoneColor
//        }?.color?.toArgb() ?: R.color.white
    return qrgEncoder.getBitmap(0)
}


@Preview
@Composable
fun CustomText(title: String = "title", value: String = "عزمي راضي عزمي") {
    Text(
        text = title, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp, color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        backgroundColor = Color.White
    ) {
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

}


@Composable
fun CustomTextFile(
    height: Int? = null, title: String = "title", data: MutableState<String>,
    keyboard: KeyboardType = KeyboardType.Text,
    isSingleLine: Boolean = true
) {
    Text(
        text = title, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp, color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        modifier = if (height != null) Modifier
            .fillMaxWidth()
            .height(150.dp) else Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        backgroundColor = Color.White
    ) {
        OutlinedTextField(
            singleLine = isSingleLine,
            value = data.value,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.Blue,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Blue
            ),
            onValueChange = {
                data.value = it
            },
            placeholder = {
                Text(
                    text = title, fontSize = 14.sp, fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = Color.DarkGray, fontWeight = FontWeight.Normal, fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboard)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

}

@Composable
fun SampleSpinner(
    hint: String, list: List<String>, selectedValue: Int, onSelectionChanged: (id: Int) -> Unit
) {
    var selected by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    if (list.isNotEmpty())
        selected = list.getOrNull(selectedValue) ?: ""
    Text(
        text = hint, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp, color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            backgroundColor = Color.White
        ) {
            Column {
                OutlinedTextField(
                    value = (selected),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color.Transparent
                    ),
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = hint, fontSize = 14.sp, fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                    readOnly = true,
                    textStyle = TextStyle(
                        color = Color.DarkGray, fontWeight = FontWeight.Normal, fontSize = 14.sp
                    )
                )
                DropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, start = 16.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    list.forEachIndexed { index, entry ->
                        DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                            onSelectionChanged(index)
                            selected = entry
                            expanded = false
                        }) {
                            Text(
                                text = (entry), modifier = Modifier.wrapContentWidth()
                            )

                        }
                    }
                }
            }

        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(onClick = { expanded = !expanded })
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

fun Bitmap.saveBitmap(
    folder: String,
    imageName: String,
    context: Context,
    onError: (String) -> Unit
): Uri? {
    var uri: Uri? = null
    val fileName = "$imageName.jpg"
    try {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM$folder/")
            // values.put(MediaStore.MediaColumns.IS_PENDING, 1)
        } else {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + folder)
            val file = File(directory, fileName)
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }
        var isExist = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
            // query for the file
            val cursor: Cursor? = context.contentResolver.query(
                extVolumeUri,
                null,
                MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.RELATIVE_PATH + " = ?",
                arrayOf(fileName, "DCIM$folder/"),
                null
            )
            if (cursor != null && cursor.count > 0) {
                // get URI
                while (cursor.moveToNext()) {
                    val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (nameIndex > -1) {
                        val displayName = cursor.getString(nameIndex)
                        if (displayName == fileName) {
                            val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                            if (idIndex > -1) {
                                val id = cursor.getLong(idIndex)
                                uri = ("$extVolumeUri/$id").toUri()
                                println(uri)
                                isExist = true
                            }
                        }
                    }
                }

                cursor.close()
            } else {
                uri =
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
            }
        } else {
            uri =
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
        }

        if (uri != null) {
            if (isExist) {
                context.contentResolver.delete(
                    uri,
                    MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.RELATIVE_PATH + " = ?",
                    arrayOf(fileName, "DCIM$folder/")
                )
                uri =
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
            }

            uri?.let {
                context.contentResolver.openOutputStream(it, "wt").use { output ->
                    this.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }
            }
        } else {
            onError("")
        }
    } catch (e: Exception) {
        onError(e.message.toString())
    }

    return uri
}

