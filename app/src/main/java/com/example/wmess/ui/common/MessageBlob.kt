package com.example.wmess.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.ui.formatters.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun BoxScope.MessageBlob(
    isReceivedType: Boolean,
    message: Message,
) {
    val modifier = remember {
        if (isReceivedType) Modifier
            .padding(start = 8.dp, end = 12.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .align(Alignment.CenterStart)
        else Modifier
            .padding(start = 12.dp, end = 8.dp)
            .background(Color(0xFFE0F7FA), RoundedCornerShape(8.dp))
            .align(Alignment.CenterEnd)
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = message.content!!)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = if (isReceivedType) Modifier else Modifier.align(Alignment.End),
                text = formatInstant(message.dateSent),
                style = TextStyle(color = Color.Gray, fontSize = TextUnit(12f, TextUnitType.Sp))
            )
        }
    }
}