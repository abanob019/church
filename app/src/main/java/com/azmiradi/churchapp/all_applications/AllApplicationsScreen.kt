package com.azmiradi.churchapp.all_applications

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.azmiradi.churchapp.exel.common.ExcelUtils
import kotlinx.coroutines.FlowPreview
import java.io.File
import java.util.*

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
            selectedIndex.value = 0
            Toast.makeText(context, "تم حفظ الملف", Toast.LENGTH_LONG).show()
            showDataOfExile.value = false
            viewModel.getApplications()
        }
    }

    state.data?.let {
        LaunchedEffect(it) {
            applicationsList.clear()
            applicationsList.addAll(it)
        }
    }
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "كل المسجلين حتي الان",
                fontSize = 18.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

    }) {
        Column(Modifier.fillMaxSize()) {
            CustomTabs(selectedIndex = selectedIndex)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                Button(modifier = Modifier
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
                            Toast.makeText(context, "تم حفظ الملف", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(context, "فشل حفظ الملف", Toast.LENGTH_LONG)
                                .show()
                        }
                    }) {
                    Text(
                        text = "تصدير XLS", fontSize = 16.sp
                    )
                }
            }
            ShowItems(applicationsList) {
                onNavigate(APPLICATION_DETAILS, it)
            }
        }
    }

    if (showDataOfExile.value) {
        Dialog(properties = DialogProperties(false, false), onDismissRequest = {
            showDataOfExile.value = false
        }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White
            ) {
                Column(Modifier.fillMaxWidth()) {
                    ShowItems(applicationsList = selectedApplications)
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
                }
            }
        }
    }
}

@Composable
fun ShowItems(
    applicationsList: SnapshotStateList<ApplicationPojo>,
    onClick: ((nationalID: String) -> Unit)? = null
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp)
    ) {
        items(applicationsList) {
            ApplicationItem(it) {
                if (onClick != null) {
                    onClick(it.nationalID ?: "")
                }
            }
        }
    }
}

@Composable
fun ApplicationItem(
    applicationPojo: ApplicationPojo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick()
            },
        backgroundColor = Color.White,
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
    selectedIndex: MutableState<Int>
) {

    TabRow(
        selectedTabIndex = selectedIndex.value,
        backgroundColor = Color(0xff1E76DA)
    ) {
        ApplicationsType.values().forEachIndexed { index, type ->
            val selected = selectedIndex.value == index
            Tab(
                modifier = if (selected) Modifier
                    .background(
                        Color.White
                    )
                else Modifier
                    .background(
                        Color(
                            0xff1E76DA
                        )
                    ),
                selected = selected,
                onClick = {
                    selectedIndex.value = index
                    viewModel.getApplications(type)
                },
                text = { Text(text = type.title, color = Color(0xff6FAAEE)) }
            )
        }
    }
}

enum class ApplicationsType(val id: Int, val title: String) {
    All(2, "الكل"), Active(0, "مفعلة"), DisActive(1, "غير مفعلة"), Attended(2, "حاضرون")
}


