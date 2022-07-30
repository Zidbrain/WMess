package com.example.wmess.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.example.wmess.R
import com.example.wmess.navigation.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.ui.common.*
import com.example.wmess.viewmodel.*
import org.koin.androidx.compose.*
import org.koin.core.parameter.*
import java.util.*

@Composable
fun CreateRoomScreen(navigator: MessengerNavigator, accessToken: String, currentUserId: UUID) {
    val viewModel: CreateRoomViewModel by viewModel() { parametersOf(accessToken, currentUserId) }

    Column {
        TopAppBar() {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 1,
                value = viewModel.searchText,
                onValueChange = { viewModel.searchText = it },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Search...", style = TextStyle(color = Color.Gray)) }
            )
        }

        val listState = rememberLazyListState()

        val users = remember { viewModel.users }

        if (users.isEmpty())
            Text(text = "No results")
        else {
            LazyColumn(
                modifier = Modifier.scrollbar(listState, horizontal = false),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigator.navigate(
                                MessengerNavTarget.MessageBoard(
                                    accessToken,
                                    currentUserId,
                                    it.id
                                )
                            )
                        }) {
                        Image(
                            painterResource(id = R.drawable.default_profile_icon_16), null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it.nickname, fontWeight = FontWeight.Bold)
                            Text(text = it.phoneNumber ?: "No phone number")
                            Text(text = it.status ?: "No status")
                        }
                    }
                }
            }
        }
    }
}