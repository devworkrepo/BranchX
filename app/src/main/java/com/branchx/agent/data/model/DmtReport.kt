package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class DmtReportResponse(
    val status : Int,
    val message : String?,
    val count : Int,
    @SerializedName("data")
    val report : List<DmtReport>
) : Parcelable

@Keep
@Parcelize
data class DmtReport(

    @SerializedName("Pansid")
    val transactionId : String,
    val PantID : String,
    @SerializedName("ProCame")
    val productName : String,
    @SerializedName("Amount")
    val amount : String,
    @SerializedName("Panate")
    val transactionDate : String,
    @SerializedName("Oprtus")
    val statusId : String,
    @SerializedName("OprStatus")
    val statusMessage : String,
    @SerializedName("Apimsg")
    val apiMessage : String,
    @SerializedName("OprrID")
    val refOperatorId : String,
    @SerializedName("AcctNo")
    val accountNumber : String,
    @SerializedName("ModDate")
    val modifiedDate : String,
    @SerializedName("Remarks1")
    val remark : String,
    @SerializedName("CurrrentStatus")
    val currentStatus : String,
    @SerializedName("MDConvenience")
    val mdConvenience : String,
    @SerializedName("DConvenience")
    val dConvenience : String,
    @SerializedName("RConvenience")
    val rConvenience : String,
    @SerializedName("ConsumerNo")
    val consumerNumber : String,
    @SerializedName("MainID")
    val mainID : String,
    @SerializedName("APITransID")
    val apiTransactionId : String,
    @SerializedName("TransSource")
    val transactionSource : String,

    var isContentInfoVisible : Boolean = false
):Parcelable