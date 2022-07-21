package com.example.wmess.model.modelclasses

import com.example.wmess.di.*
import java.util.*

data class User(val id: UUID, var nickname: String, var phoneNumber: String?, var status: String?) {
    @Transient
    private var _avatarUrl: String? = null
    val avatarURL: String
        get() {
            if (_avatarUrl == null)
                _avatarUrl = "${BASE_URL}images/$id"
            return _avatarUrl!!
        }

    fun toPatchUser(): ApiPatchUser =
        ApiPatchUser(nickname, phoneNumber, status)
}
