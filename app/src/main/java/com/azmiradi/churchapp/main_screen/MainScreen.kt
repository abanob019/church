package com.azmiradi.churchapp.main_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azmiradi.churchapp.NavigationDestination.ADD_CLASSES
import com.azmiradi.churchapp.NavigationDestination.ADD_ZONE
import com.azmiradi.churchapp.NavigationDestination.ALL_APPLICATIONS
import com.azmiradi.churchapp.NavigationDestination.REPORTS
import com.azmiradi.churchapp.NavigationDestination.SCAN_ID

@Composable
fun MainScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 50.dp, start = 50.dp), onClick = {
            onNavigate(ALL_APPLICATIONS)
        }) {
            Text(
                text = "المسجلين حتي الان",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 50.dp, start = 50.dp),
            onClick = {
                onNavigate(SCAN_ID)
            }) {
            Text(
                text = "كشف عن مستخدم",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 50.dp, start = 50.dp),
            onClick = {
                onNavigate(REPORTS)
            }) {
            Text(text = "تقارير", fontSize = 16.sp)
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
    }
}