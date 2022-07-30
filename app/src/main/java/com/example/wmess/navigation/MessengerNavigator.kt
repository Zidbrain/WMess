package com.example.wmess.navigation

import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.Rooms.Companion.ROUTE_SCHEME
import com.example.wmess.view.*
import java.util.*

class MessengerNavigator(navController: NavHostController) :
    Navigator<MessengerNavTarget>(navController) {

    open class MessengerNavTarget(route: String) : NavTarget(route) {
        companion object {
            fun getRoute(accessToken: String) = "messenger/${accessToken}"
            const val EXTERNAL_ROUTE = "messenger/{accessToken}"
        }

        data class Rooms(val accessToken: String) :
            MessengerNavTarget("rooms?accessToken=${accessToken}") {
            companion object {
                const val ROUTE_SCHEME = "rooms?accessToken={accessToken}"
            }
        }

        data class MessageBoard(val accessToken: String, val userId: UUID, val withUser: UUID) :
            MessengerNavTarget("messageBoard?accessToken=${accessToken}&userId=${userId}&withUser=${withUser}") {
            companion object {
                const val ROUTE_SCHEME =
                    "messageBoard?accessToken={accessToken}&userId={userId}&withUser={withUser}"
            }
        }

        data class CreateRoom(val accessToken: String, val currentUserId: UUID) :
            MessengerNavTarget("createRoom?accessToken=${accessToken}&userId=${currentUserId}") {
            companion object {
                const val ROUTE_SCHEME =
                    "createRoom?accessToken={accessToken}&userId={currentUserId}"
            }
        }
    }

    override fun NavGraphBuilder.navGraph() {
        navigation(
            startDestination = ROUTE_SCHEME,
            route = MessengerNavTarget.EXTERNAL_ROUTE
        ) {
            composable(ROUTE_SCHEME) {
                RoomsScreen(this@MessengerNavigator, it.getArgument("accessToken"))
            }
            composable(MessageBoard.ROUTE_SCHEME) {
                com.example.wmess.view.MessageBoard(
                    it.getArgument("accessToken"),
                    UUID.fromString(it.getArgument("userId")),
                    UUID.fromString(it.getArgument("withUser"))
                )
            }
            composable(CreateRoom.ROUTE_SCHEME) {
                CreateRoomScreen(
                    this@MessengerNavigator,
                    it.getArgument("accessToken"),
                    UUID.fromString(it.getArgument("currentUserId"))
                )
            }
        }
    }
}