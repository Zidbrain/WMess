package com.example.wmess.model.modelclasses

import com.google.gson.annotations.*
import java.util.*

data class ApiUploadFile(@SerializedName("fileHandle") val handle: UUID)