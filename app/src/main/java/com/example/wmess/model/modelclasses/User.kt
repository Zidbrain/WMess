package com.example.wmess.model.modelclasses

import com.example.wmess.*
import java.util.*

data class User(val id: UUID, var nickname: String, var phoneNumber: String?, var status: String?) {
    val avatarURL = BASE_URL + "/images/${id}"
}
