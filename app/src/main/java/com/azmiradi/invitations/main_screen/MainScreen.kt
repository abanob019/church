package com.azmiradi.invitations.main_screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.invitations.ColorsZ
import com.azmiradi.invitations.CustomTextInput
import com.azmiradi.invitations.NavigationDestination.ALL_APPLICATIONS
import com.azmiradi.invitations.NavigationDestination.ALL_ATTENDED
import com.azmiradi.invitations.ProgressBar
import com.azmiradi.invitations.R
import com.azmiradi.invitations.application_details.AESEncryption
import com.azmiradi.invitations.application_details.ApplicationDetailsViewModel
import com.azmiradi.invitations.application_details.CustomText
import com.azmiradi.invitations.login.LoginViewModel
import com.azmiradi.invitations.ui.theme.PrimaryColor
import com.azmiradi.invitations.ui.theme.SecondaryColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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

    val searchByNumber = rememberSaveable {
        mutableStateOf(false)
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
            when (viewModel.myPreferences.ruel) {
                LoginViewModel.Rule.ADMIN -> {
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

//            Spacer(modifier = Modifier.height(20.dp))
//
//            Button(modifier = Modifier
//                .fillMaxWidth()
//                .padding(end = 50.dp, start = 50.dp),
//                onClick = {
//                    onNavigate(ADD_ZONE)
//                }) {
//                Text(text = "اضافة منطقه", fontSize = 16.sp)
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Button(modifier = Modifier
//                .fillMaxWidth()
//                .padding(end = 50.dp, start = 50.dp),
//                onClick = {
//                    onNavigate(ADD_CLASSES)
//                }) {
//                Text(text = "اضافة فئة", fontSize = 16.sp)
//            }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            val options = ScanOptions()
                            options.setPrompt("Scan a barcode")
                            scanQrCodeLauncher.launch(options)
                        }) {
                        Text(text = "تسجيل حضور QR", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            searchByNumber.value = true
                        }) {
                        Text(text = "تسجيل حضور بالبحث", fontSize = 16.sp)
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
                }

                LoginViewModel.Rule.ATTENDEES -> {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            onNavigate(ALL_ATTENDED)
                        }) {
                        Text(text = "متابعة الحضور", fontSize = 16.sp)
                    }
                }

                LoginViewModel.Rule.READ_A -> {
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            val options = ScanOptions()
                            options.setPrompt("Scan a barcode")
                            scanQrCodeLauncher.launch(options)
                        }) {
                        Text(text = "تسجيل حضور QR", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            searchByNumber.value = true
                        }) {
                        Text(text = "تسجيل حضور بالبحث", fontSize = 16.sp)
                    }
                }

                LoginViewModel.Rule.READ_B -> {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 50.dp, start = 50.dp),
                        onClick = {
                            val options = ScanOptions()
                            options.setPrompt("Scan a barcode")
                            scanQrCodeLauncher.launch(options)
                        }) {
                        Text(text = "قراءة QR", fontSize = 16.sp)
                    }
                }

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

            if (!nationalId.isNullOrEmpty() &&
                (viewModel.myPreferences.ruel == LoginViewModel.Rule.READ_A ||
                        viewModel.myPreferences.ruel == LoginViewModel.Rule.ADMIN)
            ) {
                Column(
                    Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
                ) {
                    ApplicationDetailsDialog(invitationNumber = nationalId.trim(),
                        onAttend = {
                            qrData.value = ""
                        }, onBack = {
                            qrData.value = ""
                        })
                }
            } else {
                val color = remember {
                    mutableStateOf<Int?>(null)
                }
                color.value =
                    ColorsZ.values().find {
                        it.name.equals(dataArray?.lastOrNull(), true)
                    }?.colorID
                color.value?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = Color.White
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))

                            CustomText(
                                value = dataArray?.joinToString(" - ") ?: "",
                            )

                            Spacer(modifier = Modifier.height(10.dp))
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
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp, end = 24.dp), onClick = {
                                    qrData.value = ""
                                }, colors = ButtonDefaults.buttonColors(PrimaryColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.back),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = SecondaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                    }
                }
            }
        }
    }

    if (searchByNumber.value) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = false
        ), onDismissRequest = {
            searchByNumber.value = false
        }) {
            UserSearchComposable(isShow = searchByNumber)
        }
    }
}

@Composable
fun UserSearchComposable(
    viewModel: ApplicationDetailsViewModel = hiltViewModel(),
    isShow: MutableState<Boolean>
) {
    val state = viewModel.stateApplicationDetails.value
    val stateUpdate = viewModel.stateUpdateApplication.value
    val context = LocalContext.current

    ProgressBar(
        isShow = state.isLoading || stateUpdate.isLoading
    )

    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }
    if (stateUpdate.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, stateUpdate.error, Toast.LENGTH_LONG).show()
        }
    }

    val invitationNumber = rememberSaveable() {
        mutableStateOf("")
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextInput(
                hint = stringResource(id = R.string.invitation_number),
                mutableState = invitationNumber,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp), onClick = {
                    if (invitationNumber.value.isNotEmpty()) {
                        viewModel.getApplicationDetailsByInvitationID(invitationNumber.value.trim())
                    } else {
                        Toast.makeText(context, "ادخل رقم الدعوة بشكل صحيح", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, colors = ButtonDefaults.buttonColors(PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.search),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = SecondaryColor
                )
            }
            state.data?.applicationPojo?.let {
                ApplicationDetailsView(it,
                    onBack = {
                        viewModel.resetViewModel()
                        isShow.value = false
                    }, onAttend = {
                        viewModel.resetViewModel()
                        isShow.value = false
                    }, onUpdate = {
                        viewModel.updateApplication(it)
                    })
            }
        }
    }

}