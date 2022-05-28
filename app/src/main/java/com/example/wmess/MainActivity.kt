package com.example.wmess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.wmess.view.CachedLoginScreen
import com.example.wmess.view.LoginScreen
import com.example.wmess.view.RegisterScreen
import com.example.wmess.view.RoomsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityScreen()
        }
    }
}

@Composable
fun mutableStub() =
    remember { mutableStateOf("") }

fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = Screens.CachedLogin, route = "access") {
        composable(Screens.CachedLogin) { CachedLoginScreen(navController) }
        composable(Screens.Login) { LoginScreen(navController) }
        composable(Screens.Register) { RegisterScreen(navController) }
    }
}

fun NavGraphBuilder.messengerGraph(navController: NavController) {
    navigation(startDestination = "rooms/{accessToken}", route = "messenger/{accessToken}") {
        composable("rooms/{accessToken}") { RoomsScreen(accessToken = it.arguments?.getString("accessToken")!!) }
    }
}

@Composable
fun ActivityScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "access") {
        loginGraph(navController)
        messengerGraph(navController)
    }
}