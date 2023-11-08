package com.branchx.agent.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.branchx.agent.data.model.User
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class LoginResponse(
    val status: Int,
    val message: String,
    @SerializedName("data")
    val user: User)



@Keep
@Parcelize
data class AppGenerateOtp(val status: Int, val message: String,val otp : String) : Parcelable