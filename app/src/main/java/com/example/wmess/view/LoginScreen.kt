package com.example.wmess.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.wmess.viewmodel.LoginScreenUiState
import com.example.wmess.viewmodel.LoginViewModel

@Composable
private fun InputFields(viewModel: LoginViewModel) {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {

        TextInputField(
            title = stringResource(R.string.login),
            textState = viewModel.login
        )

        var isPasswordVisible by rememberSaveable {
            mutableStateOf(false)
        }
        TextInputField(
            title = stringResource(R.string.password),
            showPassword = isPasswordVisible,
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        ImageVector.vectorResource(
                            id =
                            if (isPasswordVisible)
                                R.drawable.ic_baseline_visibility_24
                            else
                                R.drawable.ic_baseline_visibility_off_24
                        ),
                        contentDescription = null
                    )
                }
            },
            textState = viewModel.password
        )
    }
}

@Composable
private fun Buttons(viewModel: LoginViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = viewModel::doLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_in))
        }
        ElevatedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.register))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = hiltViewModel()
    WMessTheme {

        val state = viewModel.uiState.value

        when (state) {
            is LoginScreenUiState.Error -> AlertDialog(
                onDismissRequest = viewModel::reset,
                icon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_baseline_error_outline_24),
                        null
                    )
                },
                confirmButton = {
                    TextButton(onClick = viewModel::reset) {
                        Text(text = stringResource(R.string.ok))
                    }
                },
                title = { Text(text = stringResource(R.string.error_title)) },
                text = {
                    Text(text = stringResource(state.errorMsg))
                })
            is LoginScreenUiState.SignedIn -> navController.navigate(Screens.Messenger(state.accessToken)) { popUpTo(0) }
            else -> {}
        }

        Scaffold {
            ConstraintLayout(
                modifier = Modifier
                    .padding(it)
                    .padding(60.dp)
                    .fillMaxSize()
            ) {
                val (column, progress) = createRefs()

                Column(
                    modifier = Modifier
                        .constrainAs(column) {
                            centerTo(parent)
                        }
                        .width(IntrinsicSize.Min)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        40.dp,
                        Alignment.CenterVertically
                    ),
                ) {
                    InputFields(viewModel = viewModel)
                    Buttons(viewModel = viewModel, navController = navController)
                }

                if (state is LoginScreenUiState.InProgress) {
                    CircularProgressIndicator(modifier = Modifier.constrainAs(progress) {
                        top.linkTo(column.bottom, 30.dp)
                        centerHorizontallyTo(parent)
                    })
                }
            }
        }
    }
}