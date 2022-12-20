package com.azmiradi.churchapp.main_screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.all_applications.ApplicationPojo
import com.azmiradi.churchapp.application_details.*

@Composable
fun ApplicationDetailsDialog(
    applicationID: String,
    onAttend: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ApplicationDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getApplicationDetails(applicationID)
    }

    val state = viewModel.stateApplicationDetails.value
    val stateUpdate = viewModel.stateUpdateApplication.value

    val context = LocalContext.current

    ProgressBar(
        isShow = state.isLoading || stateUpdate.isLoading
    )

    if (state.error.isNotEmpty() || stateUpdate.error.isNotEmpty()) {
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

        }
    }

    stateUpdate.data?.let {
        LaunchedEffect(key1 = Unit) {
            onAttend(applicationPojo?.email ?: "")
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

            if (applicationPojo?.isApproved == false && !state.isLoading) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
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
            CustomText(value = applicationPojo?.title ?: "----", title = "اللقب")
            CustomText(value = applicationPojo?.name ?: "----", title = "الاسم")
            CustomText(value = applicationPojo?.jobTitle ?: "----", title = "المسمي الوظيفي")
            CustomText(value = applicationPojo?.nationalID ?: "----", title = "الرقم القومي")
            CustomText(value = applicationPojo?.phone ?: "----", title = "رقم الهاتف")
            CustomText(value = applicationPojo?.employer ?: "----", title = "الجهة")
            if (zones.isNotEmpty())
                CustomText(value = zones[selectedZone.value].zoneName ?: "----", title = "المنطقه")
            if (classes.isNotEmpty())
                CustomText(value = classes[selectedZone.value].className ?: "----", title = "الفئة")
            CustomText(value = applicationPojo?.note ?: "----", title = "ملاحظات")
            CustomText(value = applicationPojo?.email ?: "----", title = "البريد")
            if (applicationPojo?.isApproved == true) {
                Row(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 10.dp),
                        onClick = {
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
                            applicationPojo?.isAttend = true
                            applicationPojo?.let { viewModel.updateApplication(it) }
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


@Composable
fun ApplicationDetailsDialog(data: String, color: String) {
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

            Text(text = data)

            val color = remember {
                android.graphics.Color.parseColor("#$color")
            }
            Card(
                modifier = Modifier.size(100.dp),
                backgroundColor = Color(color)
            ) {

            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}