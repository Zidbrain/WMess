package com.example.wmess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.hilt.navigation.compose.*
import com.example.wmess.R
import com.example.wmess.navigation.*
import com.example.wmess.navigation.LoginNavigator.LoginNavTarget.*
import com.example.wmess.ui.theme.*
import com.example.wmess.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CachedLoginScreen(navigator: LoginNavigator) {
    val viewModel = hiltViewModel<CachedLoginViewModel>()

    WMessTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {

                when (val state = viewModel.uiState.value) {
                    CachedLoginScreenUiState.Constructed -> viewModel.login()
                    CachedLoginScreenUiState.Error ->
                        AlertDialog(onDismissRequest = {
                            navigator.navigate(Login)
                        },
                            icon = {
                                Icon(
                                    ImageVector.vectorResource(id = R.drawable.ic_baseline_error_outline_24),
                                    null
                                )
                            },
                            title = { Text(stringResource(id = R.string.error_title)) },
                            text = { Text(stringResource(R.string.unable_to_login)) },
                            confirmButton = {
                                TextButton(onClick = { navigator.navigate(Login) }) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                            })
                    CachedLoginScreenUiState.InProgress -> CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                    is CachedLoginScreenUiState.SignedIn -> navigator.NavigateComposable(Messenger(state.accessToken))
                    CachedLoginScreenUiState.CacheMiss -> navigator.NavigateComposable(Login)
                }
            }
        }
    }
}