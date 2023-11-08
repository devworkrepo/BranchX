package com.branchx.agent.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class NeoBankingResponse(
    val status: Int?,
    val message: String?,
    @SerializedName("errorcode")
    val errorCode: Int?,
): Parcelable

@Keep
data class NeoBankingVerifyOtpResponse(
    val status: Int?,
    val message: String?,
    @SerializedName("errorcode")
    val errorCode: Int?,
    @SerializedName("usrdetails")
    val userDetails: UserDetails?,
)

data class UserDetails(
    @SerializedName("firstname")
    val firstName: String?,
    @SerializedName("lastname")
    val lastName: String?,
    @SerializedName("customerid")
    val customerId: String? = "",
    @SerializedName("balance")
    val balance: String?,
)




