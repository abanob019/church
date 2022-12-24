package com.azmiradi.churchapp.main_screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.NavigationDestination.ADD_CLASSES
import com.azmiradi.churchapp.NavigationDestination.ADD_ZONE
import com.azmiradi.churchapp.NavigationDestination.ALL_APPLICATIONS
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(), onNavigate: (String) -> Unit
) {
    val cameraPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val scannedID = rememberSaveable() {
        mutableStateOf("")
    }

    val allScannedData = rememberSaveable() {
        mutableStateOf("")
    }

    val detailsDialog = rememberSaveable() {
        mutableStateOf(false)
    }

    val detailsDialogOffline = rememberSaveable() {
        mutableStateOf(false)
    }

    val isOffline = rememberSaveable() {
        mutableStateOf(false)
    }

    val qrData = rememberSaveable() {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        when (result) {
            is QRResult.QRSuccess -> {
                println("result.content.rawValue::: "+result.content.rawValue)
//                if (isOffline.value) {
//                    detailsDialogOffline.value = true
//                    qrData.value = result.content.rawValue
//                } else {
//                    val nationalId = result.content.rawValue.split("\n")[2].trim()
//                    scannedID.value = nationalId
//                    detailsDialog.value = true
//                    allScannedData.value = result.content.rawValue
//                }
            }

            is QRResult.QRError -> {
                Toast.makeText(context, "الرمز غير صحيح", Toast.LENGTH_LONG).show()
            }

            is QRResult.QRMissingPermission -> {

            }

            is QRResult.QRUserCanceled -> {
                Toast.makeText(context, "الرمز غير صحيح", Toast.LENGTH_LONG).show()
            }
        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cameraPermissionState.allPermissionsGranted) {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    onNavigate(ALL_APPLICATIONS)
                }) {
                Text(
                    text = "المسجلين حتي الان", fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    isOffline.value = false
                    scanQrCodeLauncher.launch(null)
                }) {
                Text(
                    text = "كشف عن مستخدم", fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    onNavigate(ADD_ZONE)
                }) {
                Text(text = "اضافة منطقه", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    onNavigate(ADD_CLASSES)
                }) {
                Text(text = "اضافة فئة", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    isOffline.value = true
                    scanQrCodeLauncher.launch(null)
                }) {
                Text(text = "فحص اوفلاين", fontSize = 16.sp)
            }
        } else {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    cameraPermissionState.launchMultiplePermissionRequest()
                }) {
                Text(
                    text = "تهئية التصاريح", fontSize = 16.sp
                )
            }
        }
    }

    if (detailsDialog.value) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ), onDismissRequest = {
            detailsDialog.value = false
        }) {
            ApplicationDetailsDialog(applicationID = scannedID.value, onAttend = {
                viewModel.sendMail(allScannedData.value, it)
                detailsDialog.value = false
            }, onBack = {
                detailsDialog.value = false
            })
        }
    }

    if (detailsDialogOffline.value) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ), onDismissRequest = {
            detailsDialog.value = false
        }) {
            val color = qrData.value.split("\n")[6].trim()
            ApplicationDetailsDialog(data = qrData.value, color = color)
        }
    }
}