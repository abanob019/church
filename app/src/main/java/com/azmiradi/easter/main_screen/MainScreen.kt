package com.azmiradi.easter.main_screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.easter.NavigationDestination.ADD_CLASSES
import com.azmiradi.easter.NavigationDestination.ADD_ZONE
import com.azmiradi.easter.NavigationDestination.ALL_APPLICATIONS
import com.azmiradi.easter.NavigationDestination.ALL_ATTENDED
import com.azmiradi.easter.ProgressBar
import com.azmiradi.easter.application_details.AESEncryption
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(), onNavigate: (String) -> Unit
) {

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    val qrData = rememberSaveable {
        mutableStateOf("")
    }

    ProgressBar(isShow = viewModel.stateUpdateData.value.isLoading)

    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            qrData.value = ""
        } else {
            qrData.value = result.contents
        }
    }

    val enableOffline = rememberSaveable {
        mutableStateOf(viewModel.isOffline())
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        RadioButton(selected = enableOffline.value, onClick = {
            enableOffline.value = !enableOffline.value
            viewModel.setIsOffline(enableOffline.value)
        })
        Text(text = "Offline Mode")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cameraPermissionState.hasPermission) {
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
                    val options = ScanOptions()
                    options.setPrompt("Scan a barcode")
                    scanQrCodeLauncher.launch(options)
                }) {
                Text(text = "تسجيل حضور", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    onNavigate(ALL_ATTENDED)
                }) {
                Text(text = "متابعة الحضور", fontSize = 16.sp)
            }
        } else {
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 50.dp, start = 50.dp),
                onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }) {
                Text(
                    text = "تهئية التصاريح", fontSize = 16.sp
                )
            }
        }
    }

    if (qrData.value.isNotEmpty()) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ), onDismissRequest = {
            qrData.value = ""
        }) {
            val data = AESEncryption.decrypt(qrData.value)
            val dataArray = data?.split("\n")
            val nationalId = dataArray?.getOrNull(1)

            if (nationalId != null) {
                ApplicationDetailsDialog(invitationNumber = nationalId.trim(),
                    onAttend = {
                        qrData.value = ""

                    }, onBack = {
                        qrData.value = ""
                    })
            } else {
                println("nationalID:: null")
            }

        }
    }
}