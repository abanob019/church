package com.azmiradi.invitations.attendence

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.invitations.NavigationDestination
import com.azmiradi.invitations.ProgressBar
import com.azmiradi.invitations.all_applications.*
import com.azmiradi.invitations.exel.common.ExcelUtils
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AttendedScreen(
    viewModel: AttendedViewModel = hiltViewModel(),
    onNavigate: (String, String) -> Unit
) {
    val state = viewModel.stateApplications.value
    val context = LocalContext.current

    ProgressBar(isShow = state.isLoading)
    if (state.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "فشل اتمام العمليه", Toast.LENGTH_LONG).show()
        }
    }
    val applicationsList = remember {
        mutableStateListOf<ApplicationPojo>()
    }
    state.data?.let {
        LaunchedEffect(it) {
            applicationsList.clear()
            applicationsList.addAll(it)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getApplications()
    }

    Scaffold(topBar = {
        Column(Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "الحاضرون حتي الان",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Divider(color = Color.White, thickness = 1.dp)
            SearchView { keyWord ->
                if (keyWord.isNotEmpty()) {
                    applicationsList.clear()
                    applicationsList.addAll(state.data?.filter {
                        it.name?.contains(keyWord) == true ||
                                it.employer?.contains(keyWord) == true ||
                                it.jobTitle?.contains(keyWord) == true
                    } ?: kotlin.collections.ArrayList())
                } else {
                    applicationsList.clear()
                    state.data?.let { applicationsList.addAll(it) }
                }
            }
            Divider(color = Color.White, thickness = 1.dp)
        }
    }, content = {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(
                top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp
            )
        ) {
            item {
                Button(modifier = Modifier
                    .fillMaxWidth(),
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
            items(applicationsList) {
                ApplicationItem(applicationPojo = it) {
                    onNavigate(NavigationDestination.APPLICATION_DETAILS, it)
                }
            }
        }
    })

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationItem(
    applicationPojo: ApplicationPojo,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .combinedClickable(onClick = {
                onNavigate(applicationPojo.nationalID)
            }, onLongClick = {

            }),
        elevation = 10.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = applicationPojo.title + " " + applicationPojo.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = applicationPojo.jobTitle ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = applicationPojo.employer ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}