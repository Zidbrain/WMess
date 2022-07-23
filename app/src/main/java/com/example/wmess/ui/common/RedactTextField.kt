package com.example.wmess.ui.common

import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*

@Preview
@Composable
fun RedactTextField(
    modifier: Modifier = Modifier,
    name: String = "preview",
    mutableState: MutableState<String> = remember { mutableStateOf("") },
    onConfirmedChange: (newValue: String) -> Unit = {},
    textStyle: TextStyle = TextStyle.Default
) {
    var redactState by remember { mutableStateOf(false) }
    val (text, setText) = mutableState
    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    TextField(
        value = text,
        label = { Text(name) },
        onValueChange = setText,
        readOnly = !redactState,
        textStyle = textStyle,
        modifier = modifier.focusRequester(focusRequester),
        trailingIcon = {
            IconButton(
                onClick = {
                    if (redactState) {
                        focusRequester.freeFocus()
                        focusManager.clearFocus(true)
                        onConfirmedChange(text)
                    } else {
                        focusRequester.requestFocus()
                    }

                    redactState = !redactState
                },
            ) {
                val iconRes =
                    if (redactState)
                        Icons.Default.Check
                    else
                        Icons.Default.Edit
                Icon(iconRes, null)
            }
        }
    )
}