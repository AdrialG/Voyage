package com.voyageteam.voyage.data.response

import com.crocodic.core.api.ModelResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.voyageteam.voyage.data.User

data class LoginResponse (
    @SerializedName("data")
    @Expose
    val user: User?,
    @SerializedName("token")
    @Expose
    val token: String?
): ModelResponse()