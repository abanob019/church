package com.azmiradi.invitations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azmiradi.invitations.ui.theme.PrimaryColor
import com.azmiradi.invitations.ui.theme.SecondaryColor

@Composable
fun CustomTextInput(
    modifier: Modifier = Modifier,
    hint: String,
    mutableState: MutableState<String>,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    enable: Boolean = true,
    onClick: (() -> (Unit))? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current


    OutlinedTextField(
        enabled = enable,
        textStyle = TextStyle(
            color = SecondaryColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = PrimaryColor,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = PrimaryColor,
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = PrimaryColor
        ),
        singleLine = true,
        value = mutableState.value,
        leadingIcon = leadingIcon,
        modifier = modifier
            .clickable(role = Role.Tab) {
                onClick?.let { it() }
            },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        placeholder = {
            Text(
                text = hint, fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        },
        onValueChange = {
            mutableState.value = it
        }, trailingIcon = trailingIcon
    )
}


@Composable
fun TextInputsPassword(
    modifier: Modifier = Modifier,
    hint: String,
    isError: Boolean = false,
    mutableState: MutableState<String>,
) {
    val focusManager = LocalFocusManager.current


    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier,
        textStyle = TextStyle(
            color = SecondaryColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ), isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = PrimaryColor,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = PrimaryColor,
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = PrimaryColor
        ),
        maxLines = 1,
        value = mutableState.value,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),


        placeholder = {
            Text(
                text = hint, fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        },
        onValueChange = {
            mutableState.value = it
        }, trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(tint = PrimaryColor, imageVector = image, contentDescription = "")
            }
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()

    )
}

@Composable
fun NormalTextFiled(
    mutableState: MutableState<String>,
    hint: String,
    hintColor: Color,
    modifier: Modifier, backGroundColor: Color,
    enable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    BasicTextField(
        enabled = enable,
        textStyle = TextStyle(
            color = PrimaryColor,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
        ),
        modifier = modifier
            .clip(RoundedCornerShape(percent = 10))
            .background(Color.Transparent)
            .clickable { onClick?.let { it() } },
        value = mutableState.value,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = {
            mutableState
                .value = it
        },
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(backGroundColor)
                    .padding(10.dp)
            ) {

                AnimatedVisibility(visible = mutableState.value.isEmpty()) {
                    Text(
                        text = hint,
                        fontWeight = FontWeight.Normal, textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        color = hintColor
                    )
                }
                innerTextField()
            }
        },
    )
}


@Composable
fun SampleSpinner(
    modifier: Modifier? = null,
    hint: String,
    list: List<String>,
    onSelectionChanged: (id: String) -> Unit
) {

    var selected by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // initial value

    Box(modifier ?: Modifier.fillMaxWidth()) {
        Column {
            OutlinedTextField(
                value = (selected),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = PrimaryColor,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryColor,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = PrimaryColor
                ),
                onValueChange = { },
                placeholder = {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true,
                textStyle = TextStyle(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { entry ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSelectionChanged(entry)
                            selected = entry
                            expanded = false
                        }) {
                        Text(
                            text = (entry),
                            modifier = Modifier
                                .wrapContentWidth()
                        )

                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

@Composable
fun SampleSpinner(
    modifier: Modifier? = null,
    hint: String,
    list: List<Pair<String, String>>,
    onSelectionChanged: (id: String) -> Unit,
    modifie2r: String? = null,
) {

    var selected by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // initial value

    Box(modifier ?: Modifier.fillMaxWidth()) {
        Column {
            OutlinedTextField(
                value = (selected),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = PrimaryColor,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryColor,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = PrimaryColor
                ),
                onValueChange = { },
                placeholder = {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true,
                textStyle = TextStyle(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { entry ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSelectionChanged(entry.second)
                            selected = entry.first
                            expanded = false
                        }) {
                        Text(
                            text = (entry.first),
                            modifier = Modifier
                                .wrapContentWidth()
                        )

                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}
