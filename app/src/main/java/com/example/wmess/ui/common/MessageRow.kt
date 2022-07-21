package com.example.wmess.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.*
import coil.compose.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.ui.formatters.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MessageRow(
    withUser: User,
    message: Message,
    unreadAmount: Int,
    currentUser: User,
    imageLoader: ImageLoader
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = withUser.avatarURL,
            contentDescription = null,
            imageLoader = imageLoader,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = withUser.nickname,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(24f, TextUnitType.Sp)
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    text = formatInstant(message.dateSent),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = TextUnit(12f, TextUnitType.Sp)
                    )
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = buildAnnotatedString {
                        if (message.userFrom == currentUser.id)
                            withStyle(SpanStyle(color = Color.Blue)) {
                                append("You: ")
                            }
                        append(message.content!!)
                    },
                    style = TextStyle(Color.DarkGray, TextUnit(14F, TextUnitType.Sp)),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                if (unreadAmount != 0)
                    MessageAmountNotification(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        unreadAmount = unreadAmount
                    )
            }
        }
    }
}