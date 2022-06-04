package com.example.wmess.navigation

import androidx.navigation.*
import com.example.wmess.navigation.LoginNavigator.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.view.*

class LoginNavigator(navController: NavHostController) :
    GenericNavigator<LoginNavTarget, LoginNavigator>(navController) {

    override fun NavGraphBuilder.navGraph(navigator: LoginNavigator) {
        navigation(startDestination = LoginNavTarget.CachedLogin, route = "access") {
            composable(LoginNavTarget.CachedLogin) { CachedLoginScreen(navigator) }
            composable(LoginNavTarget.Login) { LoginScreen(navigator) }
            composable(LoginNavTarget.Register) { RegisterScreen(navigator) }
        }
    }

    open class LoginNavTarget(route: String) : GenericNavTarget(route) {
        companion object {
            const val EXTERNAL_ROUTE = "access"
        }

        object Login : LoginNavTarget("login")
        object CachedLogin : LoginNavTarget("cachedLogin")
        object Register : LoginNavTarget("Register")
        data class Messenger(val accessToken: String) : LoginNavTarget(MessengerNavTarget.getRoute(accessToken))
    }
}