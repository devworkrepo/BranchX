package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class LedgerReportResponse(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("count") var count: Int? = null,
    @SerializedName("data") var report: ArrayList<LedgerReport>? = null

) : Parcelable


@Keep
@Parcelize
data class LedgerReport(

    @SerializedName("TransId") var transactionId: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("UserID") var userId: String? = null,
    @SerializedName("MobileNo") var mobileNumber: String? = null,
    @SerializedName("TransDate") var date: String? = null,
    @SerializedName("Particulars") var Particulars: String? = null,
    @SerializedName("Dr_Cr") var creditDebit: String? = null,
    @SerializedName("OpeningBalance") var openingBalance: String? = null,
    @SerializedName("Amount") var amount: String? = null,
    @SerializedName("ClosingBalance") var closingBalance: String? = null,
    @SerializedName("TransType") var transactionType: String? = null,
    @SerializedName("UserParent") var userParent: String? = null,
    @SerializedName("ProfileName") var profileName: String? = null,
    @SerializedName("Remarks") var remark: String? = null,
    @SerializedName("ConvTyp") var ConvTyp: String? = null,
    @SerializedName("Conv") var Conv: String? = null,
    @SerializedName("DSTamt") var gstAmount: String? = null,
    @SerializedName("TDSamt") var tdsAmount: String? = null,
    @SerializedName("Tramt") var transactionAmount: String? = null,
    @SerializedName("CustCh") var CustCh: String? = null,
    @SerializedName("ReDist") var ReDist: String? = null,
    @SerializedName("Impname") var Impname: String? = null,
    @SerializedName("ImOrgName") var ImOrgName: String? = null,
    var isContentInfoVisible : Boolean = false

) : Parcelable