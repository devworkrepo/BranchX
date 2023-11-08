package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class User(
    @SerializedName("userid")
    val userId : Int,
    val name : String,
    @SerializedName("userprofile")
    val userProfile : String,
    @SerializedName("mainbalance")
    var mainBalance : String,
    @SerializedName("aepsbalance")
    var aepsBalance : String,
    @SerializedName("MATM")
    var matmBalance : String,
    @SerializedName("prfimg")
    val profilePic : String
) : Parcelable



