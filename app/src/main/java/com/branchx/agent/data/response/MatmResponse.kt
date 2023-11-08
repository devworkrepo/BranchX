package com.branchx.agent.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class MatmRequestResponse(
    val status: Int,
    val message: String,
    @SerializedName("transid")
    val txnId: String ? = null,
    @SerializedName("supermerchantid")
    val superMerchantId: String ? =null,
    @SerializedName("merchantid")
    val loginId: String? = null,
    @SerializedName("merchantpass")
    val loginPin: String?=null,
) : Parcelable

data class MatmPrefDetail(
    val merchantId : String = "",
    val merchantPin : String = "",
    val superMerchantId : String = "",
    val deviceId : String = ""
)