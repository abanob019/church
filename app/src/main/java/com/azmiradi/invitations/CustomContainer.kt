package com.azmiradi.invitations

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun CustomContainer(
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    modifier: Modifier,
    baseViewModel: BaseViewModel,
    content: @Composable ColumnScope.() -> Unit
) {
    ProgressBar(isShow = baseViewModel.isLoading())

    val context = LocalContext.current

    if (baseViewModel.toastMessage().isNotEmpty()) {
        LaunchedEffect(key1 = Unit)
        {
            Toast.makeText(context, baseViewModel.toastMessage(), Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier,
        content = content
    )
    DisposableEffect(Unit){
        onDispose {
            baseViewModel.resetState()
        }
    }
}