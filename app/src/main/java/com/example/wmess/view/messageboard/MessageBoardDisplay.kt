package com.example.wmess.view.messageboard

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.core.content.*
import androidx.core.net.*
import com.example.wmess.model.modelclasses.MessageType.*
import com.example.wmess.ui.common.*
import com.example.wmess.viewmodel.*
import java.io.*
import java.util.*

@Composable
internal fun ConstraintLayoutScope.MessageBoardDisplay(
    viewModel: MessageBoardViewModel,
    column: ConstrainedLayoutReference,
    textInput: ConstrainedLayoutReference,
    withUser: UUID,
    lazyListState: LazyListState
) {
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp),
        modifier = Modifier
            .scrollbar(lazyListState, false)
            .constrainAs(column) {
                top.linkTo(parent.top)
                bottom.linkTo(textInput.top)
                height = Dimension.fillToConstraints
            }
    ) {
        items(viewModel.history) {
            Box(Modifier.fillMaxWidth()) {
                when (it.messageType) {
                    TEXT -> MessageBlobText(
                        isReceivedType = it.userFrom == withUser,
                        message = it
                    )
                    FILE -> {
                        val fileInfo = viewModel.fileInfo[it]

                        if (fileInfo == null)
                            LaunchedEffect(it) {
                                viewModel.getFileInfo(it)
                            }

                        val context = LocalContext.current
                        val file = if (fileInfo != null)
                            File(context.filesDir, fileInfo.name)
                        else null

                        MessageBlobFile(
                            isReceivedType = it.userFrom == withUser,
                            message = it,
                            downloadProgress =
                            if (file?.exists() == true && viewModel.fileDownloadProgress[it] == null)
                                1f
                            else
                                viewModel.fileDownloadProgress[it],
                            fileInfo = fileInfo,
                            onDownload = {
                                file!!.createNewFile()
                                viewModel.downloadFile(
                                    it,
                                    context.contentResolver.openOutputStream(file.toUri())!!
                                )
                            },
                            onCancelDownload = { viewModel.cancelDownloadFile(it) }) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    FileProvider.getUriForFile(
                                        context,
                                        "com.example.wmess.provider",
                                        file!!
                                    )
                                ).addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}