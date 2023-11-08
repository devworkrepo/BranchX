package com.branchx.agent.data.model.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Notification(
   @SerializedName("Useage") val message : String
) : Parcelable