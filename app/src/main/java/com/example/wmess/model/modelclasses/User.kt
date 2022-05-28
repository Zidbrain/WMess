package com.example.wmess.model.modelclasses

import java.util.*

data class User(val id: UUID, var nickname: String, var phoneNumber: String?, var status: String?)
