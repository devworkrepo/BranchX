package com.branchx.agent.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*fund request user type response : model*/
@Parcelize
data class FundRequestUserTypeResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val userTypes : List<FundRequestUserType>
):Parcelable

@Parcelize
data class FundRequestUserType(
    @SerializedName("Userid")
    val userId : String,
    @SerializedName("MobileNo")
    val mobileNumber : String
):Parcelable



/*fund request payment mode response : model*/
@Parcelize
data class FundRequestPaymentModeResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val paymentModeList : List<FundRequestPaymentMode>
):Parcelable

@Parcelize
data class FundRequestPaymentMode(
    @SerializedName("Id")
    val id : String,
    @SerializedName("BankTransactionMode")
    val name : String
):Parcelable



/*fund request payment mode response : model*/
@Parcelize
data class FundRequestBankResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val bankList : List<FundRequestBank>
):Parcelable

@Parcelize
data class FundRequestBank(
    @SerializedName("AcNo")
    val accountNumber : String,
    @SerializedName("Bank")
    val name : String
):Parcelable


/*all response together*/

data class FundRequestInitialResponse(
    val bankResponse : FundRequestBankResponse,
    val paymentModeResponse : FundRequestPaymentModeResponse,
    val userTypeResponse : FundRequestUserTypeResponse,
)



@Parcelize
data class FundRequestReportResponse(
    val status : Int,
    val message : String,
    @SerializedName("data")
    val reports : List<FundRequestReport>
):Parcelable


@Parcelize
data class FundRequestReport(
    @SerializedName("Transid")
    val transactionId : String,
    @SerializedName("Requnt")
    val amount : String,
    @SerializedName("Reqate")
    val date : String,
    @SerializedName("Payode")
    val paymentMode : String,
    @SerializedName("Remrks")
    val remark : String,
    @SerializedName("BankDetails")
    val bank : String,
    @SerializedName("ReqStatus")
    val status : String,
    @SerializedName("Reqatus")
    val statusId : String,
    @SerializedName("Requestname")
    val requestTo : String,
    @SerializedName("RequestMob")
    val mobileNumber : String,
    @SerializedName("Userid")
    val userId : String,
    var isContentInfoVisible : Boolean = false

):Parcelable



