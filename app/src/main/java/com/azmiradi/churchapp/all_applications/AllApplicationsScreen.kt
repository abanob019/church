package com.azmiradi.churchapp.all_applications

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.churchapp.NavigationDestination.APPLICATION_DETAILS
import com.azmiradi.churchapp.ProgressBar
import com.azmiradi.churchapp.RealPathUtil
import com.azmiradi.churchapp.application_details.saveBitmap
import com.azmiradi.churchapp.exel.common.ExcelUtils
import java.io.File
import java.util.Calendar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllApplicationsScreen(
    viewModel: AllApplicationViewModel = hiltViewModel(),
    onNavigate: (String, String) -> Unit
) {


    val state = viewModel.stateApplications.value
    val context = LocalContext.current


    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
            result?.let {
              //  val item = RealPathUtil.getRealPath(context, result)
              //  println("items: "+ item)
            }

        }

    ProgressBar(isShow = state.isLoading)
    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }
    val applicationsList = remember {
        mutableStateListOf<ApplicationPojo>()
    }

    LaunchedEffect(Unit) {
        viewModel.getApplications()
        // applicationsList.addAll(ExcelUtils.readFromExcelWorkbook(context,"1670959220829.xls"))
        // println("applicationsList:: "+ applicationsList.size)
    }


    state.data?.let {
        LaunchedEffect(Unit) {
            applicationsList.clear()
            applicationsList.addAll(it)
            ExcelUtils.exportDataIntoWorkbook(context, "contacts.xls", applicationsList)
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth()) {
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
            }
            items(applicationsList) {
                ApplicationItem(it) {
                    onNavigate(APPLICATION_DETAILS, it.nationalID ?: "")
                }
            }
        }
    }

}

@Composable
fun ApplicationItem(applicationPojo: ApplicationPojo, onClick: () -> Unit) {
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