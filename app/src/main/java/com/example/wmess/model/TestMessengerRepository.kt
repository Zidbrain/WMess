package com.example.wmess.model

//import com.example.wmess.model.modelclasses.*
//import com.example.wmess.model.modelclasses.MessageType.*
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.*
//import java.time.*
//import java.util.*
//import kotlin.coroutines.*
//
//
//private val _users = mutableListOf(
//    User(UUID.randomUUID(), "Nickname1", "+79999999999", "Status1"),
//    User(UUID.randomUUID(), "Nickname2", "+79999999992", "Status2"),
//    User(UUID.randomUUID(), "Nickname3", "+79999999993", "Status3")
//)
//
//class TestMessengerRepository(accessToken: String) : MessengerRepository(accessToken) {
//    private lateinit var users: MutableList<User>
//
//    private fun reloadUsers(): List<User> {
//        users = _users
//            return users
//    }
//
//    override fun getUserById(id: UUID) =
//        users.find { it.id == id }
//
//    override suspend fun getCurrentUser(): User =
//        getUsers()[0]
//
//    override suspend fun getUsers(): List<User> {
//        if (!::users.isInitialized) {
//            reloadUsers()
//        }
//        return users
//    }
//
//    override suspend fun patchUser(user: User) {
//        users[0] = user
//        _users[0] = user
//    }
//
//    private fun getHistory(): List<Message> {
//
//        return listOf(
//            Message(users[0].id, users[1].id, TEXT, "Message1", null, Instant.now()),
//            Message(users[0].id, users[2].id, TEXT, "Message2", null, Instant.now()),
//            Message(users[1].id, users[0].id, TEXT, "Message3", null, Instant.now())
//        )
//    }
//
//    override suspend fun getHistoryByUsers(): Map<User, List<Message>> {
//        val history = getHistory()
//        val user = getCurrentUser()
//        val map = mutableMapOf<User, MutableList<Message>>()
//        history.forEach {
//            map.getOrPut(getUserById((if (it.userFrom == user.id) it.userTo else it.userFrom)!!)!!) {
//                mutableListOf()
//            }.add(it)
//        }
//        map.forEach {
//            it.value.sortBy { message -> message.sentDate }
//        }
//        return map
//    }
//
//    override val notifications: Flow<Pair<User, Message>> = flow {
//        for (i in generateSequence(3) { it + 1 }) {
//            delay(1000)
//            val from = getUsers()[1]
//
//            emit(
//                from to Message(
//                    from.id,
//                    getCurrentUser().id,
//                    TEXT,
//                    "Message$i",
//                    null,
//                    Instant.now()
//                )
//            )
//        }
//    }.shareIn(CoroutineScope(EmptyCoroutineContext), SharingStarted.Eagerly)
//}