package com.example.wmess

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.wmess.navigation.*
import com.example.wmess.navigation.LoginNavigator.*

const val BASE_URL = "https://localhost/api/"

class MainActivity : ComponentActivity() {
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