package com.example.wmess

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.wmess.navigation.*
import com.example.wmess.navigation.LoginNavigator.*
import com.example.wmess.viewmodel.*
import dagger.hilt.*
import dagger.hilt.android.*
import dagger.hilt.android.components.*

const val BASE_URL = "https://localhost/api/"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun userSettingsViewModelFactory(): UserSettingsViewModel.Factory
        fun roomsViewModelFactory(): RoomsViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityScreen()
        }
    }
}

@Composable
fun ActivityScreen() {
    val navController = rememberNavController()
    val login = remember { LoginNavigator(navController) }
    val messenger = remember { MessengerNavigator(navController) }

    NavHost(navController = navController, startDestination = LoginNavTarget.EXTERNAL_ROUTE) {
        login.buildTree(this)
        messenger.buildTree(this)
    }
}