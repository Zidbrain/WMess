package com.example.wmess.ui.common

import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import com.example.wmess.R

@Preview
@Composable
fun RedactTextField(
    modifier: Modifier = Modifier,
    name: String = "preview",
    mutableState: MutableState<String> = mutableStateOf("")
) {
    var redactState by remember { mutableStateOf(false) }
    var text by remember {
        if (redactState)
            mutableStateOf(mutableState.value)
        else
            mutableState
    }

    TextField(
        value = text,
        onValueChange = { text = it },
        readOnly = !redactState,
        modifier = modifier,
        placeholder = { Text(name) },
        trailingIcon = {
            IconButton(
                onClick = { redactState = !redactState },
            ) {
                val iconRes = rememberVectorPainter(
                    image = ImageVector.vectorResource(
                        id =
                        if (redactState) R.drawable.ic_baseline_check_24
                        else R.drawable.ic_baseline_edit_24
                    )
                )
                Icon(iconRes, null)
            }
        }
    )
}