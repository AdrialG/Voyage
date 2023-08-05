package com.voyageteam.voyage.data.response

import com.crocodic.core.api.ModelResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.voyageteam.voyage.data.User

data class RegisterResponse (
    @SerializedName("data")
    @Expose
    val user: User?
): ModelResponse()