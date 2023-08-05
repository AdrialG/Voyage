package com.voyageteam.voyage.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Destination(
    @Expose
    @SerializedName("id")
    val id: Int?,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("image")
    val image: String,
    @Expose
    @SerializedName("address")
    val address: String,
    @Expose
    @SerializedName("latitude")
    val latitude: String,
    @Expose
    @SerializedName("longitude")
    val longitude: String,
    @Expose
    @SerializedName("save_by_you")
    val save_by_you: Boolean,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String,
    @Expose
    @SerializedName("created_at")
    val created_at: String,
) : Parcelable