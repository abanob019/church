package com.azmiradi.easter.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.easter.*
import com.azmiradi.easter.R
import com.azmiradi.easter.ui.theme.PrimaryColor
import com.azmiradi.easter.ui.theme.SecondaryColor

@Composable
fun LoginScreen(
    onNavigate: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            onNavigate(NavigationDestination.MAIN)
            viewModel.resetState()
        }
    }
    CustomContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        baseViewModel = viewModel,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier.fillMaxSize().weight(1f)
        )
        Spacer(modifier = Modifier.height(90.dp))
        val username = rememberSaveable() {
            mutableStateOf("")
        }
        val passwordInput = rememberSaveable() {
            mutableStateOf("")
        }

        CustomTextInput(
            hint = stringResource(id = R.string.username),
            mutableState = username,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp),
            isError = viewModel.errorUsername.value
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextInputsPassword(
            hint = stringResource(id = R.string.password),
            mutableState = passwordInput,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp),
            isError = viewModel.errorPassword.value
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp), onClick = {
                viewModel.loginAdmin(
                    username.value, passwordInput.value
                )
            }, colors = ButtonDefaults.buttonColors(PrimaryColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SecondaryColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

}