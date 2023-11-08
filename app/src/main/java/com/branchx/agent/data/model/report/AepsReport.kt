package com.branchx.agent.data.model.report

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AepsReportResponse(
    val status: Int,
    val message: String,
    val count: Int,
    @SerializedName("data")
    val report: List<AepsReport>
):Parcelable


@Keep
@Parcelize
data class AepsReport(
    @SerializedName("Pansid")
    val id: String,
    @SerializedName("PantID")
    val serviceName: String,
    @SerializedName("Amount")
    val amount: String,
    @SerializedName("Panate")
    val date: String,
    @SerializedName("Oprtus")
    val statusId: String,
    @SerializedName("OprStatus")
    val status: String,
    @SerializedName("Ttype")
    val transactionType: String,
    @SerializedName("OprrID")
    val operatorId: String,
    @SerializedName("ConsNo")
    val customerNumber: String,
    @SerializedName("ConsNm")
    val customerName: String,
    @SerializedName("BankID")
    val bankName: String,
    @SerializedName("Apimsg")
    val message: String,
    @SerializedName("aadharn")
    val aadhaarNumber: String,
    @SerializedName("AvailableBalance")
    val availableBalance: String,
    var isContentInfoVisible : Boolean = false
):Parcelable