package com.example.wmess.view.messageboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.constraintlayout.compose.*
import com.example.wmess.viewmodel.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*
import java.util.*

@Composable
fun MessageBoard(currentUser: UUID, withUser: UUID) {
    val viewModel: MessageBoardViewModel by viewModel {
        parametersOf(
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

        MessageBoardDisplay(
            viewModel = viewModel,
            column = column,
            textInput = textInput,
            withUser = withUser,
            lazyListState = lazyListState
        )
        TextInput(viewModel = viewModel, textInput = textInput)
    }
}