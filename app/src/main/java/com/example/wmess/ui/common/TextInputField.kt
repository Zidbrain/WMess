package com.example.wmess.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextInputField(
    title: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    showPassword: Boolean = true,
    textState: MutableState<String>
) {
    Column(modifier = Modifier.wrapContentSize()) {
        Text(text = title)

        val text = remember { textState }

        OutlinedTextField(
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            value = text.value,
            placeholder = { Text(text = title) },
            onValueChange = text::value::set,
            trailingIcon = trailingIcon,
        )
    }
}