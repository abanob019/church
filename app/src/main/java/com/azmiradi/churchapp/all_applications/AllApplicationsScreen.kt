package com.azmiradi.churchapp.all_applications

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllApplicationsScreen(
    viewModel: AllApplicationViewModel = hiltViewModel(),
    onNavigate: (String,String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getApplications()
    }

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
        LaunchedEffect(Unit) {
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 5.dp, end = 10.dp, start = 10.dp, bottom = 10.dp)
        ) {

            items(applicationsList) {
                ApplicationItem(it) {
                    onNavigate(APPLICATION_DETAILS,it.nationalID?:"")
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