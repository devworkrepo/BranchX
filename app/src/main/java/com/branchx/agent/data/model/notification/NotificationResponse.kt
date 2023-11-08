package com.branchx.agent.data.model.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
@Keep
data class NotificationResponse(
    val status : Int,
    val count : Int,
    val message : String,
    @SerializedName("data") val notifications : List<Notification>?
) :Parcelable