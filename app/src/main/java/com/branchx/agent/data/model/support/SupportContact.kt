package com.branchx.agent.data.model.support

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SupportContact(
    @SerializedName("Id") val id : Int?,
    @SerializedName("Namame") val name : String?,
    @SerializedName("MobeNo") val mobileNumber : String?,
    @SerializedName("EmalId") val emailId : String,
    @SerializedName("Conype") val role : String?,
    @SerializedName("Locion") val location : String?
) : Parcelable