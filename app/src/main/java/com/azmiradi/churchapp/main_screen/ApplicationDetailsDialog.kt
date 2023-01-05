package com.azmiradi.churchapp.main_screen

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.ColorsZ
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.R
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.azmiradi.churchapp.application_details.*
import com.azmiradi.churchapp.local_database.Zone
import com.azmiradi.churchapp.local_database.Zones
import java.util.Calendar

@Composable
fun ApplicationDetailsDialog(
    invitationNumber: String,
    onAttend: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ApplicationDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getApplicationDetailsByInvitation(invitationNumber)
    }

    val state = viewModel.stateApplicationDetails.value
    val stateUpdate = viewModel.stateUpdateApplication.value

    val context = LocalContext.current

    ProgressBar(
        isShow = state.isLoading || stateUpdate.isLoading
    )

    if (stateUpdate.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }

    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            viewModel.resetViewModel()
            onBack()
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


    val color = remember {
        mutableStateOf<Int?>(null)
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
                if (data.zoneName == applicationPojo?.zoneID) {
                    selectedZone.value = index
                }
            }

            color.value =
                ColorsZ.values().find {
                    it.name.equals(applicationPojo?.zoneColorName?.trim() ?: "black", true)
                }?.colorID
        }
    }

    stateUpdate.data?.let {
        LaunchedEffect(key1 = Unit) {
            onAttend(applicationPojo?.email ?: "")
            viewModel.resetViewModel()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(end = 20.dp, start = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (applicationPojo?.isApproved == false && !state.isLoading) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, Color.Red),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "هذه الدعو غير مفعله",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            CustomText(
                value = (applicationPojo?.title ?: "----") + "/" + (applicationPojo?.name
                    ?: "----"), title = "الاسم"
            )
            CustomText(value = applicationPojo?.jobTitle ?: "----")
            CustomText(value = applicationPojo?.employer ?: "----")

            CustomText(
                value = (applicationPojo?.seat ?: "----") + "-" + (applicationPojo?.row
                    ?: "----") + "-" + (applicationPojo?.zoneColorName
                    ?: "----") + "-" + (applicationPojo?.zoneCode ?: "----"), title = "Zone Details"
            )

            Spacer(modifier = Modifier.height(10.dp))


            color.value?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .border(5.dp, Color.Black)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                )
            }


            //CustomText(value = applicationPojo?.nationalID ?: "----", title = "الرقم القومي")
            //  CustomText(value = applicationPojo?.phone ?: "----", title = "رقم الهاتف")

//            if (zones.isNotEmpty())
//                CustomText(value = zones[selectedZone.value].zoneName ?: "----", title = "المنطقه")
//            if (classes.isNotEmpty())
//                CustomText(value = classes[selectedZone.value].className ?: "----", title = "الفئة")
            //  CustomText (value = applicationPojo?.note ?: "----", title = "ملاحظات"

            if (applicationPojo?.isApproved == true) {
                Row(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 10.dp),
                        onClick = {
                            viewModel.resetViewModel()
                            onBack()
                        }) {
                        Text(
                            text = "رجوع", fontSize = 16.sp
                        )
                    }

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                        onClick = {
                            if (applicationPojo?.isAttend != true) {
                                applicationPojo?.isAttend = true
                                applicationPojo?.attendDate = Calendar.getInstance().timeInMillis
                                applicationPojo?.let { viewModel.updateApplication(it) }
                            } else {
                                onAttend(applicationPojo?.email ?: "")
                            }
                        }) {
                        Text(
                            text = "تسجيل حضور", fontSize = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

