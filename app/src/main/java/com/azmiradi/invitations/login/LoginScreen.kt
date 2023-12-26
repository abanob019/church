package com.azmiradi.invitations.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.invitations.*
import com.azmiradi.invitations.R
import com.azmiradi.invitations.ui.theme.PrimaryColor
import com.azmiradi.invitations.ui.theme.SecondaryColor

@Composable
fun LoginScreen(
    onNavigate: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            onNavigate(NavigationDestination.MAIN)
            viewModel.resetState()
        }
    }
    CustomContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        baseViewModel = viewModel,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(90.dp))
        val username = rememberSaveable() {
            mutableStateOf("")
        }
        val passwordInput = rememberSaveable() {
            mutableStateOf("")
        }

        CustomTextInput(
            hint = stringResource(id = R.string.email),
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
                if (username.value.isNotEmpty() && passwordInput.value.isNotEmpty()) {
                    viewModel.loginAdmin(
                        username.value.trim(), passwordInput.value
                    )
                } else {
                    Toast.makeText(context, "ادخل البيانات بشكل صحيح", Toast.LENGTH_SHORT).show()
                }
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