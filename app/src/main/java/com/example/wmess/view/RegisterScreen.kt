package com.example.wmess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.hilt.navigation.compose.*
import com.example.wmess.R
import com.example.wmess.navigation.*
import com.example.wmess.navigation.LoginNavigator.LoginNavTarget.*
import com.example.wmess.ui.common.*
import com.example.wmess.ui.theme.*
import com.example.wmess.viewmodel.*

@Composable
private fun InputFields(viewModel: RegisterViewModel) {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        TextInputField(
            title = stringResource(id = R.string.login),
            textState = viewModel.login
        )
        TextInputField(
            title = stringResource(id = R.string.username),
            textState = viewModel.username
        )
        TextInputField(
            title = stringResource(id = R.string.password),
            textState = viewModel.password
        )
    }
}

@Composable
private fun RegisterButton(viewModel: RegisterViewModel) {
    ElevatedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = viewModel::register
    ) {
        Text(text = stringResource(id = R.string.register))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navigator: LoginNavigator) {
    val viewModel = hiltViewModel<RegisterViewModel>()

    WMessTheme {
        val state = viewModel.uiState.value

        when (state) {
            is RegisterScreenUiState.Error -> {
                AlertDialog(
                    onDismissRequest = viewModel::reset,
                    confirmButton = {
                        TextButton(onClick = viewModel::reset) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_error_outline_24),
                            null
                        )
                    },
                    title = { Text(stringResource(id = R.string.error_title)) },
                    text = { Text(text = stringResource(state.errorMsg)) }
                )
            }
            is RegisterScreenUiState.Register -> navigator.NavigateComposable(
                navTarget = Messenger(
                    state.accessToken
                )
            ) { popUpTo(0) }
            else -> {}
        }

        Scaffold {
            ConstraintLayout(
                modifier = Modifier
                    .padding(it)
                    .padding(60.dp)
                    .fillMaxSize(),
            ) {
                val (column, progress) = createRefs()

                Column(
                    modifier = Modifier
                        .constrainAs(column) { centerTo(parent) }
                        .width(IntrinsicSize.Min)
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    InputFields(viewModel = viewModel)
                    RegisterButton(viewModel = viewModel)
                }

                if (state is RegisterScreenUiState.InProgress) {
                    CircularProgressIndicator(modifier = Modifier.constrainAs(progress) {
                        top.linkTo(column.bottom, 30.dp)
                        centerHorizontallyTo(parent)
                    })
                }
            }
        }
    }
}