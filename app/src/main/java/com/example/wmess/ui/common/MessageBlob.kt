package com.example.wmess.ui.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import coil.compose.*
import com.example.wmess.R
import com.example.wmess.model.modelclasses.*
import com.example.wmess.model.modelclasses.MessageType.*
import com.example.wmess.ui.formatters.*
import org.koin.androidx.compose.*
import java.io.*
import java.time.*

@Composable
@Preview
private fun MessageBlobPreview() {
    Box(modifier = Modifier.fillMaxWidth()) {
        MessageBlobFile(
            isReceivedType = true,
            message = Message(null, null, TEXT, "kdaskdl.png", null, Instant.now()),
            0.5f,
            FileInfo("dadsad", 1024 * 1024),
            {},
            {},
            {}
        )
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun BoxScope.MessageBlobCore(
    isReceivedType: Boolean,
    message: Message,
    content: @Composable (message: Message) -> Unit
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
            content(message)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = if (isReceivedType) Modifier else Modifier.align(Alignment.End),
                text = formatInstant(message.dateSent),
                style = TextStyle(color = Color.Gray, fontSize = TextUnit(12f, TextUnitType.Sp))
            )
        }
    }
}

@Composable
fun BoxScope.MessageBlobText(
    isReceivedType: Boolean,
    message: Message,
) {
    MessageBlobCore(isReceivedType = isReceivedType, message = message) {
        Text(text = it.content!!)
    }
}

@Composable
fun BoxScope.MessageBlobFile(
    isReceivedType: Boolean,
    message: Message,
    downloadProgress: Float?,
    fileInfo: FileInfo?,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    openFile: () -> Unit
) {
    MessageBlobCore(isReceivedType = isReceivedType, message = message) {
        Crossfade(
            targetState = fileInfo,
        ) {
            when (it) {
                null -> Box(modifier = Modifier.size(150.dp)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    val extension = it.name.substringAfter('.')
                    val showImage =
                        downloadProgress == 1f && (extension == "png" || extension == "jpg")

                    if (showImage) {
                        AsyncImage(
                            model = File(LocalContext.current.filesDir, it.name),
                            contentDescription = null,
                            imageLoader = get(),
                            modifier = Modifier.clickable(onClick = openFile)
                        )
                    } else {

                        Box(modifier = Modifier.size(150.dp)) {
                            if (downloadProgress != null && downloadProgress != 1f) {
                                val progress by animateFloatAsState(downloadProgress)
                                CircularProgressIndicator(
                                    progress,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(48.dp)
                                )
                            }
                            Text(
                                text = formatContentSize(it.length),
                                modifier = Modifier.align(Alignment.TopEnd)
                            )

                            IconButton(modifier = Modifier.align(Alignment.Center),
                                onClick = {
                                    if (downloadProgress == null) onDownload()
                                    else if (downloadProgress != 1f) onCancelDownload()
                                    else openFile()
                                }
                            )
                            {
                                Icon(
                                    painterResource(
                                        id = if (downloadProgress == null) R.drawable.ic_file_download_48
                                        else if (downloadProgress != 1f) R.drawable.ic_close_48
                                        else R.drawable.ic_file_open_48
                                    ),
                                    null,
                                    modifier = Modifier.background(Color.White, CircleShape)
                                )
                            }

                            Text(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                text = it.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}