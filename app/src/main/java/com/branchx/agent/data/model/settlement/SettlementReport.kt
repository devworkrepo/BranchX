package com.branchx.agent.data.model.settlement

import com.google.gson.annotations.SerializedName

data class SettlementReport(

    @SerializedName("TransId")
    val id : String?,
    @SerializedName("Name")
    val name : String?,
    @SerializedName("UserID")
    val userId : String?,
    @SerializedName("MobileNo")
    val mobileNumber : String?,
    @SerializedName("TransDate")
    val date : String?,
    @SerializedName("Particulars")
    val particulars : String?,
    @SerializedName("Dr_Cr")
    val debitOrCredit : String?,
    @SerializedName("OpeningBalance")
    val openingBalance : String?,
    @SerializedName("Amount")
    val amount : String?,
    @SerializedName("ClosingBalance")
    val closingBalance : String?,
    @SerializedName("TransType")
    val transactionType : String?,
    @SerializedName("UserParent")
    val userParent : String?,
    @SerializedName("Remarks")
    val remark : String?,
    @SerializedName("ImOrgName")
    val organizationName : String?,
    var isContentInfoVisible : Boolean = false
)

data class SettlementReportResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val settlementReports : List<SettlementReport> ? = null
)