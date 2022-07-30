package com.example.wmess.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import com.example.wmess.R
import com.example.wmess.ui.common.*
import com.example.wmess.viewmodel.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*
import java.util.*

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MessageBoard(accessToken: String, currentUser: UUID, withUser: UUID) {
    val viewModel: MessageBoardViewModel by viewModel {
        parametersOf(
            accessToken,
            currentUser,
            withUser
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFFBBDEFB),
                    1f to Color(0xFF7986CB)
                )
            )
    ) {
        val lazyListState = rememberLazyListState()
        val (column, textInput) = createRefs()

        LaunchedEffect(Unit) {
            snapshotFlow { viewModel.history.lastIndex }
                .filter { it >= 0 }
                .collect { lazyListState.animateScrollToItem(it) }
        }

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp),
            modifier = Modifier
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    bottom.linkTo(textInput.top)
                    height = Dimension.fillToConstraints
                }
                .scrollbar(lazyListState, false)
        ) {
            items(viewModel.history) {
                Box(Modifier.fillMaxWidth()) {
                    MessageBlob(isReceivedType = it.userFrom == withUser, message = it)
                }
            }
        }

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
                placeholder = { Text(text = "Message") },
                textStyle = TextStyle(fontSize = TextUnit(16f, TextUnitType.Sp)),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_attach_file_24), null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_photo_camera_24), null,
                            modifier = Modifier.size(28.dp)
                        )
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
                onClick = viewModel::send) {
                Icon(Icons.Default.Send, null, tint = Color.White)
            }
        }
    }
}