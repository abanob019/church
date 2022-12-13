package com.azmiradi.churchapp.application_details

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.R
import com.azmiradi.churchapp.RealPathUtil
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.StandardCharsets


@Composable
fun ApplicationDetailsScreen(
    applicationID: String,
    onNavigation: (destination: String) -> Unit,
    viewModel: ApplicationDetailsViewModel = hiltViewModel()
) {
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

    val qrImage = remember {
        mutableStateOf<Bitmap?>(null)
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
                if (data.zoneID == applicationPojo?.zoneID) {
                    selectedZone.value = index
                }
            }

            note.value = applicationPojo?.note ?: ""

            if (applicationPojo?.isApproved == true) {
                coroutineScope.launch(Dispatchers.IO) {
                    qrImage.value =
                        context.createQRCode(applicationPojo, zone = zones[selectedZone.value])
                }
            }
        }
    }


    updateState.data?.let {
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                qrImage.value = context.createQRCode(
                    applicationPojo = applicationPojo, zone = zones[selectedZone.value]
                )
            }
            Toast.makeText(context, "تم تحديث البيانات", Toast.LENGTH_LONG).show()
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
            CustomText(value = applicationPojo?.title ?: "----", title = "اللقب")
            CustomText(value = applicationPojo?.name ?: "----", title = "الاسم")
            CustomText(value = applicationPojo?.jobTitle ?: "----", title = "المسمي الوظيفي")
            CustomText(value = applicationPojo?.nationalID ?: "----", title = "الرقم القومي")
            CustomText(value = applicationPojo?.phone ?: "----", title = "رقم الهاتف")
            CustomText(value = applicationPojo?.employer ?: "----", title = "الجهة")
            SampleSpinner(
                "المنطقه", list = zones.mapNotNull { it.zoneName }, selectedZone.value
            ) {
                selectedZone.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner(
                "الفئه", list = classes.mapNotNull {
                    it.className
                }, selectedClass.value
            ) {
                selectedClass.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = note, title = "كتابة ملاحظات")

            if (applicationPojo?.isApproved == true) {
                Spacer(modifier = Modifier.height(10.dp))

                qrImage.value?.asImageBitmap()?.let {
                    Image(bitmap = it, contentDescription = "")
                }

                Row(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 10.dp),
                        onClick = {
                            val uri = qrImage.value?.saveBitmap(
                                applicationPojo?.name + "_" + applicationPojo?.nationalID, context
                            )
                            uri?.let {
                                viewModel.sendMail(
                                    applicationPojo?.email ?: "abanob019@gmail.com",
                                    File(RealPathUtil.getRealPath(context, it).toString())
                                )
                            }

                        }) {
                        Text(
                            text = "حفظ ال QR", fontSize = 16.sp
                        )
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                        onClick = {
                            applicationPojo?.zoneID = zones[selectedZone.value].zoneID
                            applicationPojo?.isApproved = true
                            applicationPojo?.note = note.value
                            applicationPojo?.className = classes[selectedClass.value].className

                            applicationPojo?.let { viewModel.updateApplication(it) }
                        }) {
                        Text(
                            text = "تحديث البيانات", fontSize = 16.sp
                        )
                    }
                }

            } else {
                Spacer(modifier = Modifier.height(20.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    applicationPojo?.zoneID = zones[selectedZone.value].zoneID
                    applicationPojo?.isApproved = true
                    applicationPojo?.note = note.value
                    applicationPojo?.className = classes[selectedClass.value].className
                    applicationPojo?.let { viewModel.updateApplication(it) }
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

fun Context.createQRCode(applicationPojo: ApplicationPojo?, zone: Zone): Bitmap {
    val data = java.lang.StringBuilder("الحدث: قداس عيد الميلاد المجيد 2023").append("\n")
        .append(applicationPojo?.title + " : " + applicationPojo?.name).append("\n")
        .append("الرقم القومي: ").append(applicationPojo?.nationalID).append("\n")
        .append("الوظيفة: ").append(applicationPojo?.jobTitle).append("\n").append("الجهة: ")
        .append(applicationPojo?.employer).append("\n").append("منطقة الجلوس: ")
        .append(zone.zoneName)

    val options = QrOptions.Builder(1024).setPadding(.3f).setLogo(
        QrLogo(
            drawable = DrawableSource.Resource(R.mipmap.ic_launcher),
            size = .25f,
            padding = QrLogoPadding.Accurate(.2f),
            shape = QrLogoShape.Circle
        )
    ).setColors(
        QrColors(
            dark = QrColor.Solid(getColor(R.color.black)),
            highlighting = QrColor.Solid(0xddffffff.toColor()),
        )
    ).setElementsShapes(
        QrElementsShapes(
            darkPixel = QrPixelShape.RoundCorners(),
            ball = QrBallShape.RoundCorners(.25f),
            frame = QrFrameShape.RoundCorners(.25f),
        )
    ).build()

    val generator = QrCodeGenerator(this)
    return generator.generateQrCode(
        QrData.Text(data.toString()), options, StandardCharsets.UTF_8
    )
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
fun CustomTextFile(title: String = "title", data: MutableState<String>) {
    Text(
        text = title, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp, color = Color.DarkGray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        backgroundColor = Color.White
    ) {
        OutlinedTextField(value = data.value, colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.Transparent,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Color.Transparent
        ), onValueChange = {
            data.value = it
        }, placeholder = {
            Text(
                text = title, fontSize = 14.sp, fontWeight = FontWeight.Normal
            )
        }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(
            color = Color.DarkGray, fontWeight = FontWeight.Normal, fontSize = 14.sp
        ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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
    if (list.isNotEmpty()) selected = list[selectedValue]
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

fun Bitmap.saveBitmap(imageName: String, context: Context): Uri? {
    var uri: Uri? = null
    try {
        val fileName = "$imageName.jpg"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
            //values.put(MediaStore.MediaColumns.IS_PENDING, 1)
        } else {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(directory, fileName)
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }
        uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(
                uri
            ).use { output ->
                this.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }
            Toast.makeText(context, "تم حفظ الــQR", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "فشل حفظ الــQR", Toast.LENGTH_LONG).show()
        }

    } catch (e: Exception) {
        Toast.makeText(context, e.toString() + "فشل حفظ الــQR", Toast.LENGTH_LONG).show()

    }

    return uri
}




