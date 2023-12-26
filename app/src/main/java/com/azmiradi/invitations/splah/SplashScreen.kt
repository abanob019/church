package com.azmiradi.invitations.splah

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azmiradi.invitations.NavigationDestination
import kotlinx.coroutines.delay
import com.azmiradi.invitations.R

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(4000L)
        onNavigate(NavigationDestination.LOGIN)
//        if (viewModel.isLogin()) {
//            onNavigate(MAIN)
//        } else {
//            onNavigate(NavigationDestination.LOGIN)
//        }
    }


    // Image
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "تطبيق دعوات وحضور عيد القيامة ٢٠٢٣",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontWeight = FontWeight(600),
            textAlign = TextAlign.Center
        )

        Text(
            text = "جميع الحقوق محفوظة للجنة الدعوات @٢٠٢٣",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            textAlign = TextAlign.Center
        )

    }
}