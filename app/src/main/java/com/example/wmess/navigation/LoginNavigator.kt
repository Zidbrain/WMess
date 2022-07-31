package com.example.wmess.navigation

import androidx.navigation.*
import com.example.wmess.navigation.LoginNavigator.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.view.*

class LoginNavigator(navController: NavHostController) :
    Navigator<LoginNavTarget>(navController) {

    override fun NavGraphBuilder.navGraph() {
        navigation(startDestination = LoginNavTarget.CachedLogin, route = "access") {
            composable(LoginNavTarget.CachedLogin) { CachedLoginScreen(this@LoginNavigator) }
            composable(LoginNavTarget.Login) { LoginScreen(this@LoginNavigator) }
            composable(LoginNavTarget.Register) { RegisterScreen(this@LoginNavigator) }
        }
    }

    open class LoginNavTarget(route: String) : NavTarget(route) {
        companion object {
            const val EXTERNAL_ROUTE = "access"
        }

        object Login : LoginNavTarget("login")
        object CachedLogin : LoginNavTarget("cachedLogin")
        object Register : LoginNavTarget("Register")
        object Messenger : LoginNavTarget(MessengerNavTarget.EXTERNAL_ROUTE)
    }
}