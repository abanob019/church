package com.azmiradi.churchapp.all_applications

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.azmiradi.churchapp.NavigationDestination.APPLICATION_DETAILS
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.R
import com.azmiradi.churchapp.application_details.CustomTextFile
import com.azmiradi.churchapp.application_details.SampleSpinner
import com.azmiradi.churchapp.exel.common.ExcelUtils
import com.azmiradi.churchapp.ui.theme.SelectItemColor
import kotlinx.coroutines.FlowPreview
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@OptIn(FlowPreview::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllApplicationsScreen(
    viewModel: AllApplicationViewModel = hiltViewModel(),
    onNavigate: (String, String) -> Unit
) {
    val state = viewModel.stateApplications.value
    val stateSaveData = viewModel.stateUpdateData.value

    val selectedIndex = remember { mutableStateOf(0) }

    val context = LocalContext.current

    val showDataOfExile = rememberSaveable() {
        mutableStateOf(false)
    }

    val selectedApplications = remember {
        mutableStateListOf<ApplicationPojo>()
    }

    val showDetermineApplicationsInfoDialog = remember {
        mutableStateOf(false)
    }
    val hiltModules = remember {
        HandlePathOz(context, object : HandlePathOzListener.SingleUri {
            override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
                val file = File(pathOz.path)
                val data = ExcelUtils.readFromExcelWorkbook(file)
                if (data.isNotEmpty()) {
                    selectedApplications.clear()
                    selectedApplications.addAll(data)
                    showDataOfExile.value = true
                }
            }
        })
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
            result?.let {
                hiltModules.getRealPath(it)
            }
        }

    ProgressBar(isShow = state.isLoading || stateSaveData.isLoading)
    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }

    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل حفظ الملف", Toast.LENGTH_LONG).show()
        }
    }
    val applicationsList = remember {
        mutableStateListOf<ApplicationPojo>()
    }

    LaunchedEffect(Unit) {
        viewModel.getApplications()
    }

    stateSaveData.data?.let {
        LaunchedEffect(it) {
            viewModel.resetData()
            selectedIndex.value = 0
            showDataOfExile.value = false
            showDetermineApplicationsInfoDialog.value = false
            selectedApplications.clear()
            viewModel.getApplications()
            Toast.makeText(context, "تم حفظ الملف", Toast.LENGTH_LONG).show()
        }
    }

    state.data?.let {
        LaunchedEffect(it) {
            applicationsList.clear()
            applicationsList.addAll(it)
        }
    }

    Scaffold(topBar = {
        Column(Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "كل المسجلين حتي الان",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Divider(color = Color.White, thickness = 1.dp)
            SearchView {
                viewModel.getApplications(type = ApplicationsType.values().find {type->
                    selectedIndex.value == type.id
                } ?: ApplicationsType.All, it)
            }
            Divider(color = Color.White, thickness = 1.dp)
        }


    }) {
        Column(Modifier.fillMaxSize()) {
            CustomTabs(selectedIndex = selectedIndex, selectedItems = selectedApplications)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 10.dp),
                    onClick = {
                        launcher.launch("application/vnd.ms-excel")
                    }) {
                    Text(
                        text = "استيراد XLS", fontSize = 16.sp
                    )
                }

                Button(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 10.dp),
                    onClick = {
                        val list = ExcelUtils.exportDataIntoWorkbook(
                            context,
                            Calendar.getInstance().timeInMillis.toString(),
                            applicationsList
                        )
                        if (list) {
                            Toast.makeText(context, "تم حفظ الملف", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "فشل حفظ الملف", Toast.LENGTH_LONG).show()
                        }
                    }) {
                    Text(
                        text = "تصدير XLS", fontSize = 16.sp
                    )
                }
            }

            if (selectedApplications.isNotEmpty() && selectedIndex.value != ApplicationsType.All.id) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), onClick = {
                    if ((selectedIndex.value == ApplicationsType.Active.id)) {
                        viewModel.updateData(selectedApplications.map {
                            it.copy(isApproved = false)
                        }, 0)

                    } else {
                        showDetermineApplicationsInfoDialog.value = true
                    }
                }) {
                    Text(
                        text = if (selectedIndex.value == ApplicationsType.Active.id) "الغاء التفعيل"
                        else "تفعيل", fontSize = 16.sp
                    )
                }
            }
            ShowItems(selectedIndex, selectedApplications, applicationsList, onClick = {
                onNavigate(APPLICATION_DETAILS, it)
            })
        }
    }

    if (showDataOfExile.value) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ), onDismissRequest = {
            showDataOfExile.value = false
        }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 10.dp),
                            onClick = {
                                showDataOfExile.value = false
                            }) {
                            Text(
                                text = "الغاء", fontSize = 16.sp
                            )
                        }

                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 10.dp),
                            onClick = {
                                viewModel.updateData(selectedApplications.toList(), 0)
                            }) {
                            Text(
                                text = "حفظ", fontSize = 16.sp
                            )
                        }
                    }
                    ShowItems(applicationsList = selectedApplications)
                }
            }
        }
    }

    if (showDetermineApplicationsInfoDialog.value) {
        Dialog(properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ), onDismissRequest = {
            showDetermineApplicationsInfoDialog.value = false
        }) {
            DetermineApplicationsInfoDialog(onClickConfirm = { zone, classes, not ->
                viewModel.updateData(selectedApplications.map {
                    it.copy(
                        isApproved = true, zoneID = zone, className = classes, note = not
                    )
                }, 0)
                selectedIndex.value = 0
                showDataOfExile.value = false
                showDetermineApplicationsInfoDialog.value = false
                selectedApplications.clear()
                viewModel.getApplications()
                Toast.makeText(context, "تم حفظ البيانات", Toast.LENGTH_LONG).show()
            }, onClickCancel = {
                showDetermineApplicationsInfoDialog.value = false
            })
        }
    }
}

@Composable
fun ShowItems(
    selectable: MutableState<Int>,
    selectedItems: SnapshotStateList<ApplicationPojo>,
    applicationsList: SnapshotStateList<ApplicationPojo>,
    onClick: (nationalID: String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(
            top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp
        )
    ) {
        items(applicationsList) {
            ApplicationItem(
                selectable = selectable.value == ApplicationsType.Active.id || selectable.value == ApplicationsType.DisActive.id,
                applicationPojo = it,
                selectedApplications = selectedItems,
                onLongClick = onClick
            )
        }
    }
}


@Composable
fun ShowItems(
    applicationsList: SnapshotStateList<ApplicationPojo>,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp)
    ) {
        items(applicationsList) {
            ApplicationItem(selectable = false, applicationPojo = it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationItem(
    applicationPojo: ApplicationPojo,
    selectable: Boolean = false,
    onLongClick: ((nationalID: String) -> Unit)? = null,
    selectedApplications: SnapshotStateList<ApplicationPojo>? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .combinedClickable(onClick = {
                if (selectedApplications?.contains(applicationPojo) == true) selectedApplications.remove(
                    applicationPojo
                )
                else selectedApplications?.add(applicationPojo)

            }, onLongClick = {
                if (onLongClick != null) {
                    onLongClick(applicationPojo.nationalID ?: "")
                }
            }),
        backgroundColor = if (selectable && selectedApplications?.contains(applicationPojo) == true) SelectItemColor else Color.White,
        elevation = 10.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
        ) {

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = applicationPojo.title + " " + applicationPojo.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400
            )
            Spacer(modifier = Modifier.width(12.dp))

        }
    }
}

@Composable
fun CustomTabs(
    viewModel: AllApplicationViewModel = hiltViewModel(),
    selectedIndex: MutableState<Int>,
    selectedItems: SnapshotStateList<ApplicationPojo>
) {

    TabRow(
        selectedTabIndex = selectedIndex.value, backgroundColor = Color(0xff1E76DA)
    ) {
        ApplicationsType.values().forEachIndexed { index, type ->
            val selected = selectedIndex.value == index
            Tab(modifier = if (selected) Modifier.background(
                Color.White
            )
            else Modifier.background(
                Color(
                    0xff1E76DA
                )
            ), selected = selected, onClick = {
                selectedItems.clear()
                selectedIndex.value = index
                viewModel.getApplications(type)
            }, text = { Text(text = type.title, color = Color(0xff6FAAEE)) })
        }
    }
}


@Composable
fun DetermineApplicationsInfoDialog(
    onClickConfirm: (zoneID: String, classes: String, note: String) -> Unit,
    onClickCancel: () -> Unit,
    viewModel: AllApplicationViewModel = hiltViewModel()
) {
    val selectedZone = remember {
        mutableStateOf(0)
    }

    val selectedClass = remember {
        mutableStateOf(0)
    }

    val note = remember {
        mutableStateOf("")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "من فضلك اختر الاعدادات التي سوف تطبق علي جميع الطلبات ")

            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner("المنطقه",
                list = viewModel.stateZones.value.data?.mapNotNull { it.zoneName } ?: ArrayList(),
                selectedZone.value) {
                selectedZone.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            SampleSpinner("الفئه", list = viewModel.stateClasses.value.data?.mapNotNull {
                it.className
            } ?: kotlin.collections.ArrayList(), selectedClass.value) {
                selectedClass.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextFile(data = note, title = "كتابة ملاحظات")
            Spacer(modifier = Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth()) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 10.dp),
                    onClick = {
                        onClickConfirm(
                            viewModel.stateZones.value.data?.getOrNull(selectedZone.value)?.zoneName
                                ?: "",
                            viewModel.stateClasses.value.data?.getOrNull(selectedClass.value)?.className
                                ?: "",
                            note.value
                        )
                    }) {
                    Text(
                        text = "ارسال", fontSize = 16.sp
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                    onClick = onClickCancel
                ) {
                    Text(
                        text = "الغاء", fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

    }
}


enum class ApplicationsType(val id: Int, val title: String) {
    All(0, "الكل"), Active(1, "مفعلة"), DisActive(2, "غير مفعلة"), Attended(3, "حاضرون")
}


@Composable
fun SearchView(onChange: (String) -> Unit) {
    val state = remember {
        mutableStateOf(TextFieldValue(""))
    }
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
            onChange(value.text)
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(onClick = {
                    onChange("")
                    state.value =
                        TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = colorResource(id = R.color.purple_500),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

