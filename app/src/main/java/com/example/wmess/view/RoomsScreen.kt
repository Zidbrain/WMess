@file:OptIn(ExperimentalMaterialApi::class)

package com.example.wmess.view

import android.net.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.SnackbarResult.*
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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.content.*
import coil.compose.*
import coil.request.*
import com.example.wmess.R.*
import com.example.wmess.navigation.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.*
import com.example.wmess.ui.common.*
import com.example.wmess.ui.theme.*
import com.example.wmess.viewmodel.*
import com.example.wmess.viewmodel.UiState.*
import com.google.accompanist.swiperefresh.*
import org.koin.androidx.compose.*
import java.io.*

@Composable
private fun TopBar(
    navigator: MessengerNavigator,
    viewModel: UserSettingsViewModel,
    isMenuOpen: MutableState<Boolean>
) {
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
        },
        actions = {
            if (viewModel.uiState.collectAsState().value == Loaded)
                IconButton(onClick = { navigator.navigate(CreateRoom(viewModel.currentUser.id)) }) {
                    Icon(Icons.Default.Add, "LOL")
                }
        }
    )
}

@Composable
private fun RoomsBoard(
    viewModel: RoomsViewModel,
    navigator: MessengerNavigator,
    snackbarHostState: SnackbarHostState,
    reload: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = reload
    ) {
        when (val state = viewModel.uiState) {
            Loading -> isRefreshing = true
            Loaded -> {
                val messages = viewModel.rooms
                val listState = rememberLazyListState()

                isRefreshing = false

                val connectionError = viewModel.connectionError
                if (connectionError != null) {
                    LaunchedEffect("Connection Snackbar") {
                        if (snackbarHostState.showSnackbar(
                                connectionError.message.orEmpty(),
                                "Retry"
                            ) == ActionPerformed
                        )
                            reload()
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(end = 8.dp),
                    modifier = Modifier.scrollbar(listState, false),
                ) {
                    items(messages.toList(), key = { it.first.id }) {
                        val (user, messageInfo) = it
                        Box(modifier = Modifier.clickable {
                            viewModel.readMessages(user)
                            navigator.navigate(
                                MessageBoard(
                                    viewModel.currentUser!!.id,
                                    user.id
                                )
                            )
                        }) {
                            val (message, unread) = messageInfo

                            MessageRow(
                                imageLoader = viewModel.imageLoader,
                                withUser = user,
                                message = message,
                                unreadAmount = unread,
                                currentUser = viewModel.currentUser!!
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
            is Error -> {
                isRefreshing = false
                val error = stringResource(id = string.error_message, state.errorReason.orEmpty())
                LaunchedEffect(key1 = "ShowError") {
                    if (snackbarHostState.showSnackbar(error, "Retry") == ActionPerformed)
                        reload()
                }
            }
            Initialized -> LaunchedEffect("Rooms") {
                viewModel.connect()
                viewModel.loadRooms()
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun SettingsMenu(
    viewModel: UserSettingsViewModel,
    snackbarHostState: SnackbarHostState,
    reload: () -> Unit
) {
    Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
        when (val state = viewModel.uiState.collectAsState().value) {
            Loading -> {}
            is Error -> {
                val error = stringResource(id = state.errorMsg, state.errorReason.orEmpty())
                LaunchedEffect(key1 = "ShowError") {
                    if (snackbarHostState.showSnackbar(error, "Retry") == ActionPerformed)
                        reload()
                }
            }
            Initialized -> LaunchedEffect("Load") {
                viewModel.loadFields()
            }
            Loaded ->
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box {
                            val context = LocalContext.current

                            val model = remember {
                                mutableStateOf(
                                    ImageRequest.Builder(context)
                                        .data(viewModel.currentUser.avatarURL)
                                        .build()
                                )
                            }
                            AsyncImage(
                                model = model.value,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                imageLoader = viewModel.imageLoader,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(96.dp),
                            )

                            var imageUri: Uri? = null
                            val launcher =
                                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                                    if (it)
                                        viewModel.changeAvatar(imageUri!!, context, model)
                                }

                            IconButton(
                                onClick = {
                                    val imageFile = File(context.filesDir, "avatar.png")
                                    imageUri = FileProvider.getUriForFile(
                                        context,
                                        "com.example.wmess.provider",
                                        imageFile
                                    )

                                    launcher.launch(imageUri)
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.White, CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    painterResource(id = drawable.ic_baseline_add_a_photo_24),
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
}

@Composable
fun RoomsScreen(navigator: MessengerNavigator) {
    val settingsViewModel: UserSettingsViewModel by viewModel()
    val roomsViewModel: RoomsViewModel by viewModel()

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

        val snackbarHostState = remember { SnackbarHostState() }
        val reload = {
            settingsViewModel.loadFields()
            roomsViewModel.loadRooms()
            roomsViewModel.reconnect()
        }

        BackdropScaffold(
            appBar = { TopBar(navigator, settingsViewModel, menuOpen) },
            scaffoldState = scaffoldState,
            backLayerContent = { SettingsMenu(settingsViewModel, snackbarHostState, reload) },
            frontLayerContent = {
                RoomsBoard(
                    roomsViewModel,
                    navigator,
                    snackbarHostState,
                    reload
                )
            },
            gesturesEnabled = false,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState, snackbar = {
                    Snackbar(snackbarData = it)
                })
            }
        )
    }
}