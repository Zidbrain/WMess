package com.example.wmess.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wmess.R
import com.example.wmess.Screens
import com.example.wmess.ui.theme.WMessTheme
import com.example.wmess.viewmodel.CachedLoginScreenUiState
import com.example.wmess.viewmodel.CachedLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CachedLoginScreen(navController: NavController) {
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
                            navController.navigate(Screens.Login)
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
                                TextButton(onClick = { navController.navigate(Screens.Login) }) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                            })
                    CachedLoginScreenUiState.InProgress -> CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                    is CachedLoginScreenUiState.SignedIn -> navController.navigate(Screens.Messenger(state.accessToken))
                    CachedLoginScreenUiState.CacheMiss -> navController.navigate(Screens.Login)
                }
            }
        }
    }
}