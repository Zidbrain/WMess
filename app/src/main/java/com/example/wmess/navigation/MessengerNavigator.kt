package com.example.wmess.navigation

import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.Rooms.Companion.ROUTE_SCHEME
import com.example.wmess.view.*

class MessengerNavigator(navController: NavHostController) :
    Navigator<MessengerNavTarget>(navController) {

    open class MessengerNavTarget(route: String) : NavTarget(route) {
        companion object {
            fun getRoute(accessToken: String) = "messenger/${accessToken}"
            const val EXTERNAL_ROUTE = "messenger/{accessToken}"
        }

        data class Rooms(val accessToken: String) : MessengerNavTarget("rooms?accessToken=${accessToken}") {
            companion object {
                const val ROUTE_SCHEME = "rooms?accessToken={accessToken}"
            }
        }

        data class MessageBoard(val accessToken: String, val user: User) :
            MessengerNavTarget("messageBoard?accessToken=${accessToken}&userId=${user.id}") {
            companion object {
                const val ROUTE_SCHEME = "messageBoard?accessToken={accessToken}&userId={userId}"
            }
        }
    }

    override fun NavGraphBuilder.navGraph() {
        navigation(
            startDestination = ROUTE_SCHEME,
            route = MessengerNavTarget.EXTERNAL_ROUTE
        ) {
            composable(ROUTE_SCHEME) {
                RoomsScreen(
                    this@MessengerNavigator,
                    it.arguments!!["accessToken"]!! as String
                )
            }
            composable(MessengerNavTarget.MessageBoard.ROUTE_SCHEME) { MessageBoard() }
        }
    }
}