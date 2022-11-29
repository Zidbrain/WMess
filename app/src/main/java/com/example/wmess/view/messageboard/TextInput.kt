@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wmess.view.messageboard

import androidx.activity.compose.*
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import com.example.wmess.*
import com.example.wmess.R.*
import com.example.wmess.ui.common.*
import com.example.wmess.viewmodel.*

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ConstraintLayoutScope.TextInput(
    viewModel: MessageBoardViewModel,
    textInput: ConstrainedLayoutReference
) {
    ConstraintLayout(modifier = Modifier
        .constrainAs(textInput) {
            bottom.linkTo(parent.bottom)
        }
        .fillMaxWidth()
        .padding(8.dp)) {
        val (text, send) = createRefs()

        TextField(
            modifier = Modifier.constrainAs(text) {
                start.linkTo(parent.start)
                end.linkTo(send.start)
                width = Dimension.fillToConstraints
            },
            value = viewModel.textInput, onValueChange = { viewModel.textInput = it },
            placeholder = when (viewModel.fileUploadProgress == null) {
                true -> {
                    { Text(text = "Message") }
                }
                false -> {
                    {
                        FileUploadPreview(
                            filename = viewModel.filename,
                            progress = viewModel.fileUploadProgress!!,
                            onDelete = viewModel::cancelUpload
                        )
                    }
                }
            },
            textStyle = TextStyle(fontSize = TextUnit(16f, TextUnitType.Sp)),
            shape = RoundedCornerShape(24.dp),
            trailingIcon = when (viewModel.fileUploadProgress == null) {
                false -> null
                true -> {
                    {
                        Row(
                            modifier = Modifier.padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val context = LocalContext.current
                            val pickFileLauncher = rememberLauncherForActivityResult(
                                contract = GetContent(),
                                onResult = {
                                    if (it != null) {
                                        viewModel.uploadFile(
                                            context.contentResolver.openInputStream(it)!!,
                                            context.contentResolver.getFileName(it)!!
                                        )
                                    }
                                }
                            )

                            IconButton(onClick = { pickFileLauncher.launch("*/*") }) {
                                Icon(
                                    painterResource(id = drawable.ic_baseline_attach_file_24),
                                    null,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painterResource(id = drawable.ic_baseline_photo_camera_24),
                                null,
                                modifier = Modifier.size(28.dp)
                            )

                        }
                    }
                }
            }
        )
        IconButton(
            modifier = Modifier
                .constrainAs(send) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 12.dp)
                .background(Color(0xFF3949AB), CircleShape)
                .size(58.dp),
            onClick = viewModel::send,
            enabled = viewModel.fileUploadProgress == null || viewModel.fileUploadProgress == 1f) {
            Icon(Icons.Default.Send, null, tint = Color.White)
        }
    }
}