package com.voyageteam.voyage.api

import com.voyageteam.voyage.data.response.LoginResponse
import com.voyageteam.voyage.data.response.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    @POST("auth/refresh")
    suspend fun refreshToken(): String

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email_or_phone") emailOrPhone: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterResponse

    @GET("auth/me")
    suspend fun getprofile(
    ) : String

    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfileName(
        @Field("name") name: String
    ): String

    @Multipart
    @POST("user/profile")
    suspend fun updateProfile(
        @Part("name") name: String,
        @Part photo: MultipartBody.Part?
    ): String

    @FormUrlEncoded
    @POST("auth/change-password")
    suspend fun updatePassword(
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): String

    @GET("destinations")
    suspend fun destinationList(
    ) : String

    @DELETE("auth/logout")
    suspend fun logout(
    ) : String

}