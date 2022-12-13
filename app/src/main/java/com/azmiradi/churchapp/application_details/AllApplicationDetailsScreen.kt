package com.azmiradi.churchapp.application_details

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.all_applications.ApplicationPojo

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
    val context = LocalContext.current

    ProgressBar(
        isShow = state.isLoading ||
                viewModel.stateUpdateApplication.value.isLoading
    )

    if (state.error.isNotEmpty() || viewModel.stateUpdateApplication.value.error.isNotEmpty()) {
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
        }
    }

    viewModel.stateUpdateApplication.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "تم تحديث البيانات", Toast.LENGTH_LONG).show()
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
                "المنطقه",
                list = zones.mapNotNull { it.zoneName },
                selectedZone.value
            ) {
                selectedZone.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner(
                "الفئه",
                list = classes.mapNotNull {
                    it.className
                }, selectedClass.value
            ) {
                selectedClass.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = note, title = "كتابة ملاحظات")
            Spacer(modifier = Modifier.height(20.dp))

            if (applicationPojo?.isApproved == true)
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 10.dp),
                        onClick = {

                        }) {
                        Text(
                            text = "حفظ ال QR", fontSize = 16.sp
                        )
                    }

                    Button(
                        modifier = Modifier
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
            else
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
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
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
        OutlinedTextField(
            value = data.value,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Transparent
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
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

}

@Composable
fun SampleSpinner(
    hint: String, list: List<String>,
    selectedValue: Int,
    onSelectionChanged: (id: Int) -> Unit
) {
    var selected by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    if (list.isNotEmpty())
        selected = list[selectedValue]
    Text(
        text = hint,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 14.sp,
        color = Color.DarkGray
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
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
    Spacer(modifier = Modifier.height(10.dp))


}


