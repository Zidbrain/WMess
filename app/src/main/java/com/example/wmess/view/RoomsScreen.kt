@file:OptIn(ExperimentalMaterialApi::class)

package com.example.wmess.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.example.wmess.R
import com.example.wmess.ui.common.*
import com.example.wmess.ui.theme.*
import com.example.wmess.viewmodel.*
import com.example.wmess.viewmodel.UserSettingsViewModel.UiState.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*
import androidx.compose.material3.CircularProgressIndicator as ProgressIndicator

@Composable
private fun TopBar(isMenuOpen: MutableState<Boolean>) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        title = { Text("WMess") },
        navigationIcon = {
            IconButton(onClick = { isMenuOpen.value = !isMenuOpen.value }) {
                if (!isMenuOpen.value)
                    Icon(Icons.Default.Menu, null)
                else
                    Icon(Icons.Default.Close, null)
            }
        })
}

@Composable
private fun RoomsBoard(viewModel: RoomsViewModel) {
    when (viewModel.uiState.collectAsState().value) {
        RoomsViewModel.UiState.Loading -> LaunchedEffect("Rooms") {
            viewModel.loadRooms()
        }
        RoomsViewModel.UiState.Loaded -> {
            val messages = viewModel.rooms.collectAsState()
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(end = 8.dp),
                modifier = Modifier.scrollbar(listState, false),
            ) {
                items(messages.value.toList(), key = { it.first.id }) {
                    val (user, messageFlow) = remember { it }
                    Box(modifier = Modifier.clickable { viewModel.readMessages(user) }) {
                        val unread by viewModel.unreadAmount.collectAsState().value[user]!!.collectAsState()
                        val message by messageFlow.collectAsState()

                        MessageRow(
                            avatar = painterResource(id = R.drawable.default_profile_icon_16),
                            username = user.nickname,
                            message = message,
                            unreadAmount = unread,
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    0.5f to Color.Black,
                                    1f to Color.Transparent
                                ),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.5f
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun SettingsMenu(viewModel: UserSettingsViewModel) {
    when (viewModel.uiState.collectAsState().value) {
        Loading -> {
            Box {
                ProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            LaunchedEffect("Load") {
                viewModel.loadFields()
            }
        }
        else ->
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box {
                        Image(
                            painterResource(id = R.drawable.default_profile_icon_16),
                            null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(96.dp)
                        )
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .background(Color.White, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ic_baseline_add_a_photo_24),
                                null,
                                modifier = Modifier.offset((-2).dp)
                            )

                        }
                    }
                    RedactTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(CenterVertically),
                        name = "Nickname",
                        mutableState = viewModel.fields.nickname,
                        onConfirmedChange = { viewModel.postFields() },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp, CenterVertically)
                ) {
                    RedactTextField(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Status",
                        mutableState = viewModel.fields.status,
                        onConfirmedChange = { viewModel.postFields() },
                    )
                    RedactTextField(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Phone Number",
                        mutableState = viewModel.fields.phoneNumber,
                        onConfirmedChange = { viewModel.postFields() },
                    )
                }
            }
    }
}

@Composable
@Preview
fun RoomsScreen(accessToken: String = "") {
    val settingsViewModel: UserSettingsViewModel by viewModel { parametersOf(accessToken) }
    val roomsViewModel: RoomsViewModel by viewModel { parametersOf(accessToken) }

    WMessTheme {
        val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
        val menuOpen = remember { mutableStateOf(false) }

        if (menuOpen.value && scaffoldState.isConcealed)
            LaunchedEffect("Reveal") {
                scaffoldState.reveal()
            }
        else if (!menuOpen.value && scaffoldState.isRevealed)
            LaunchedEffect("Conceal") {
                scaffoldState.conceal()
            }

        BackdropScaffold(
            appBar = { TopBar(menuOpen) },
            scaffoldState = scaffoldState,
            backLayerContent = { SettingsMenu(settingsViewModel) },
            frontLayerContent = { RoomsBoard(roomsViewModel) },
            gesturesEnabled = false
        ) {
        }
    }
}