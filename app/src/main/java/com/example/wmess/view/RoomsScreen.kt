@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wmess.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.example.wmess.R
import com.example.wmess.ui.common.*
import com.example.wmess.ui.theme.*

@Composable
private fun TopBar() {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        title = { Text("WMess") },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(ImageVector.vectorResource(id = R.drawable.ic_baseline_menu_24), null)
            }
        })
}

@Composable
@Preview
private fun SettingsMenu() {
    Column(horizontalAlignment = Alignment.Start) {
        Row {
            Image(
                painterResource(id = R.drawable.ic_launcher_foreground),
                null,
                modifier = Modifier.clip(
                    CircleShape
                )
            )
            Column(
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Text("Nickname", fontWeight = FontWeight.Bold)
                Text("djaksdjalksjdlakwjdsaa;lskdfmasdasd;kwasdkwlda")
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
        ) {
            RedactTextField(modifier = Modifier.fillMaxWidth(), name = "Status")
            RedactTextField(modifier = Modifier.fillMaxWidth(), name = "Phone Number")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun RoomsScreen() {
    WMessTheme {
        val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)
        BackdropScaffold(
            appBar = { TopBar() },
            scaffoldState = scaffoldState,
            backLayerContent = { SettingsMenu() },
            frontLayerContent = {}) {
        }
    }
}