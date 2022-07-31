package com.example.wmess.navigation

import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wmess.navigation.MessengerNavigator.*
import com.example.wmess.navigation.MessengerNavigator.MessengerNavTarget.*
import com.example.wmess.view.*
import java.util.*

class MessengerNavigator(navController: NavHostController) :
    Navigator<MessengerNavTarget>(navController) {

    open class MessengerNavTarget(route: String) : NavTarget(route) {
        companion object {
            const val EXTERNAL_ROUTE = "messenger"
        }

        object Rooms : MessengerNavTarget("rooms")

        data class MessageBoard(val userId: UUID, val withUser: UUID) :
            MessengerNavTarget("messageBoard?userId=${userId}&withUser=${withUser}") {
            companion object {
                const val ROUTE_SCHEME =
                    "messageBoard?userId={userId}&withUser={withUser}"
            }
        }

        data class CreateRoom(val currentUserId: UUID) :
            MessengerNavTarget("createRoom?userId=${currentUserId}") {
            companion object {
                const val ROUTE_SCHEME =
                    "createRoom?userId={currentUserId}"
            }
        }
    }

    override fun NavGraphBuilder.navGraph() {
        navigation(
            startDestination = Rooms.route,
            route = MessengerNavTarget.EXTERNAL_ROUTE
        ) {
            composable(Rooms.route) {
                RoomsScreen(this@MessengerNavigator)
            }
            composable(MessageBoard.ROUTE_SCHEME) {
                com.example.wmess.view.MessageBoard(
                    UUID.fromString(it.getArgument("userId")),
                    UUID.fromString(it.getArgument("withUser"))
                )
            }
            composable(CreateRoom.ROUTE_SCHEME) {
                CreateRoomScreen(
                    this@MessengerNavigator,
                    UUID.fromString(it.getArgument("currentUserId"))
                )
            }
        }
    }
}