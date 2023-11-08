package com.branchx.agent.data.model.settlement

import com.google.gson.annotations.SerializedName

data class SettlementRequest(
    @SerializedName("Pansid")
    val id : String ?,
    @SerializedName("Panate")
    val date : String ?,
    @SerializedName("transsts")
    val status : String ?,
    @SerializedName("Userid")
    val userId : String ?,
    @SerializedName("Namame")
    val name : String ?,
    @SerializedName("OrgName")
    val organizationName : String ?,

    @SerializedName("Amount")
    val amount : String ?,
    @SerializedName("RequestTo")
    val requestTo : String ?,
    @SerializedName("ReqName")
    val requestToName : String ?,
    @SerializedName("ReqOrgName")
    val requestToOrganizationName : String ?,
    @SerializedName("TransType")
    val transactionType : String ?,
    @SerializedName("Remarks")
    val remark : String ?,
    var isContentInfoVisible : Boolean = false
)

data class SettlementRequestResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val settlementRequestList : List<SettlementRequest>? = null
)