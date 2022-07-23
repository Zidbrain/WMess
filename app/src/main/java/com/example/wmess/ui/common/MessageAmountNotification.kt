package com.example.wmess.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlin.math.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MessageAmountNotification(modifier: Modifier, unreadAmount: Int) {
    Box(
        modifier = modifier then Modifier
            .background(Color(0xFF3F51B5), shape = CircleShape)
            .layout() { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val circleSize = max(placeable.height, placeable.width) + 16

                layout(circleSize, circleSize) {
                    placeable.placeRelative(
                        (circleSize - placeable.width) / 2,
                        (circleSize - placeable.height) / 2
                    )
                }
            }) {
        Text(
            text = unreadAmount.toString(),
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(Color.White, fontSize = TextUnit(16f, TextUnitType.Sp))
        )
    }
}