package com.azmiradi.churchapp.dialogs

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.FirebaseConstants.CLASSES
import com.azmiradi.churchapp.FirebaseConstants.ZONE
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.application_details.Classes
import com.azmiradi.churchapp.application_details.Zone
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun AddZoneDialog(viewModel: DialogsViewModel = hiltViewModel(),onDismiss: () -> Unit) {
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
    val zoneName = rememberSaveable() {
        mutableStateOf("")
    }

    val zoneColor = rememberSaveable() {
        mutableStateOf("")
    }
    val controller = rememberColorPickerController()

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
                value = zoneName.value, onValueChange = {
                    zoneName.value = it
                }, placeholder = {
                    Text(text = "اكتب اسم المنطقه")
                })
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "اختر لون المنطقه",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    zoneColor.value = colorEnvelope.hexCode
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier
                .fillMaxWidth(), onClick = {
                viewModel.addData(
                    Zone(zoneName = zoneName.value, zoneColor = zoneColor.value),
                    ZONE
                )
            }) {
                Text(
                    text = "ارسال",
                    fontSize = 16.sp
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
                value = className.value, onValueChange = {
                    className.value = it
                }, placeholder = {
                    Text(text = "اكتب اسم الفئه")
                })

            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier
                .fillMaxWidth(), onClick = {
                viewModel.addData(Classes(className.value), CLASSES)
            }) {
                Text(
                    text = "ارسال",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}