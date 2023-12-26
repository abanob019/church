package com.azmiradi.invitations.dialogs

import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.invitations.ColorsZ
import com.azmiradi.invitations.FirebaseConstants.CLASSES
import com.azmiradi.invitations.FirebaseConstants.ZONE
import com.azmiradi.invitations.ProgressBar
import com.azmiradi.invitations.application_details.Classes
import com.azmiradi.invitations.application_details.saveBitmap
import com.azmiradi.invitations.local_database.Zone

@Composable
fun AddZoneDialog(viewModel: DialogsViewModel = hiltViewModel(), onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.getAllZones()
    }

    val state = viewModel.addState.value
    val context = LocalContext.current
    ProgressBar(isShow = state.isLoading || viewModel.stateZones.value.isLoading)
    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }
    val zonesList = remember {
        mutableStateListOf<Zone>()
    }

    state.data?.let {
        LaunchedEffect(Unit) {
            val data = "عيد الميلادالمجيد ٢٠٢٣" +
                    "\n" +
                    zonesList.last().zoneName +
                    "\n" +
                    zonesList.last().code +
                    "\n" +
                    zonesList.last().zoneColor +
                    "\n" + zonesList.last().zoneID


            val qrgEncoder = QRGEncoder(data, null, QRGContents.Type.TEXT, 150)
            qrgEncoder.getBitmap(0).saveBitmap(
                "/Invitations/ColorsQRs",
                zonesList.last().zoneName + "_" +  zonesList.last().code + "_" +  zonesList.last().zoneColor,
                context
            )
            {
                println("DADADAD : " + it)
            }
            onDismiss()
            Toast.makeText(context, "تمت العمليه بنجاح", Toast.LENGTH_LONG).show()
        }
    }


    viewModel.stateZones.value.data?.let {
        LaunchedEffect(Unit) {
            zonesList.clear()
            zonesList.addAll(it)
        }
    }

    val zoneName = rememberSaveable() {
        mutableStateOf("")
    }

    val zoneCode = rememberSaveable() {
        mutableStateOf("")
    }

    val zoneColor = rememberSaveable() {
        mutableStateOf(ColorsZ.White.name)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = zoneName.value,
                onValueChange = {
                    zoneName.value = it
                },
                placeholder = {
                    Text(text = "اكتب اسم المنطقه")
                })
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = zoneCode.value,
                onValueChange = {
                    zoneCode.value = it
                },
                placeholder = {
                    Text(text = "كود المنطقة")
                })

            Text(
                text = "اختر لون المنطقه",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(10.dp))

            zoneColor.value = colors()
            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (zoneName.value.isNotEmpty() && zoneCode.value.isNotEmpty()) {
                    zonesList.add(
                        Zone(
                            zoneName = zoneName.value,
                            zoneColor = zoneColor.value,
                            zoneID = zonesList.last().zoneID+1,
                            code = zoneCode.value
                        )
                    )
                    viewModel.addList(zonesList, ZONE)
                } else {
                    Toast.makeText(context, "ادخل الاسم", Toast.LENGTH_LONG).show()
                }
            }) {
                Text(
                    text = "حفظ", fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AddClassDialog(viewModel: DialogsViewModel = hiltViewModel(), onDismiss: () -> Unit) {
    val state = viewModel.addState.value
    val context = LocalContext.current
    ProgressBar(isShow = state.isLoading)
    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }
    state.data?.let {
        LaunchedEffect(Unit) {
            onDismiss()
            Toast.makeText(context, "تمت العمليه بنجاح", Toast.LENGTH_LONG).show()
        }
    }
    val className = rememberSaveable() {
        mutableStateOf("")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = className.value,
                onValueChange = {
                    className.value = it
                },
                placeholder = {
                    Text(text = "اكتب اسم الفئه")
                })

            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (className.value.isNotEmpty())
                    viewModel.addData(Classes(className.value), CLASSES)
                else
                    Toast.makeText(context, "ادخل الاسم", Toast.LENGTH_LONG).show()
            }) {
                Text(
                    text = "ارسال", fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun colors(): String {
    val selectedColor = rememberSaveable() {
        mutableStateOf(ColorsZ.White.name)
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), content = {
            items(ColorsZ.values()) { item ->
                Card(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            selectedColor.value = item.name
                        },
                    elevation = 8.dp,
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = item.colorID),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (selectedColor.value == item.name) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "",
                                modifier = Modifier.padding(16.dp),
                                tint = if (item != ColorsZ.White) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        })
    return selectedColor.value
}