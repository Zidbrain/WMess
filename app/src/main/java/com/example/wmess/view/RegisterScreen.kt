package com.example.wmess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wmess.R
import com.example.wmess.Screens
import com.example.wmess.ui.common.TextInputField
import com.example.wmess.ui.theme.WMessTheme
import com.example.wmess.viewmodel.RegisterScreenUiState
import com.example.wmess.viewmodel.RegisterViewModel

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
fun RegisterScreen(navController: NavController) {
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
            is RegisterScreenUiState.Register -> navController.navigate(Screens.Messenger(state.accessToken)) { popUpTo(0) }
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