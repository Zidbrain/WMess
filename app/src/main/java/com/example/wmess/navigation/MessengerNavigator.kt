package com.example.wmess.navigation

import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.Rooms.Companion.ROUTE_SCHEME
import com.example.wmess.view.*

class MessengerNavigator(navController: NavHostController) :
    GenericNavigator<MessengerNavTarget, MessengerNavigator>(navController) {

    open class MessengerNavTarget(route: String) : GenericNavTarget(route) {
        companion object {
            const val EXTERNAL_ROUTE = "messenger/{accessToken}"
        }

        data class Rooms(val accessToken: String) : MessengerNavTarget("rooms/${accessToken}") {
            companion object {
                const val ROUTE_SCHEME = "rooms/{accessToken}"
            }
        }
    }

    override fun NavGraphBuilder.navGraph(navigator: MessengerNavigator) {
        navigation(
            startDestination = ROUTE_SCHEME,
            route = MessengerNavTarget.EXTERNAL_ROUTE
        ) {
            composable(ROUTE_SCHEME) { RoomsScreen() }
        }
    }
}