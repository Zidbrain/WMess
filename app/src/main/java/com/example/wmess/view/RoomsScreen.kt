@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.wmess.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wmess.R
import com.example.wmess.ui.common.DecoratedTextInputField
import com.example.wmess.ui.theme.WMessTheme

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
        Row() {
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
        DecoratedTextInputField()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun RoomsScreen(accessToken: String? = null) {
    WMessTheme {
        BackdropScaffold(
            appBar = { TopBar() },
            peekHeight = 0.dp,
            backLayerContent = { SettingsMenu() },
            scaffoldState = BackdropScaffoldState(BackdropValue.Revealed),
            frontLayerContent = {}) {
        }
    }
}