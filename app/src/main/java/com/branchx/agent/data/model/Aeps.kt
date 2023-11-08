package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class AepsBank(
    @SerializedName("ID")
    val id: String,
    @SerializedName("Name")
    val name: String
) : Parcelable

@Keep
@Parcelize
data class AepsBankListResponse(
    @SerializedName("data")
    val aepsBank: List<AepsBank>?=null,
    val message: String,
    val status: Int,
    val count : Int?,
) : Parcelable

@Keep
class AepsStateResponse : ArrayList<AepsStateResponseItem>()

@Keep
data class AepsStateResponseItem(
    val id: String,
    val name: String
)

@Keep
@Parcelize
data class EKycResponse(
    val status: String? = null,
    val code: Int,
    val message: String? = null,
    val encodeFPTxnId: String? = null,
    val primaryKeyId: String? = null
) : Parcelable


@Keep
@Parcelize
data class AepsTransactionResponse(
    val status : Int,
    val message : String,
    @SerializedName("Amount")
    val transactionAmount : String,
    @SerializedName("AvilableBalance")
    val availableAmount : String,
    @SerializedName("TransID")
    val transactionId : String,
    @SerializedName("TransDate")
    val transactionTime : String="",
    var aadhaarNumber : String = "",
    var mobileNumber : String="",
    var transactionType : String = "",
    var miniStatement : List<MiniStatement>
) : Parcelable


///fake mode

@Parcelize
@Keep
data class BankListResponse(
    val code: Int,
    val message: String,
    val bankFakes: List<BankFake>
) : Parcelable

@Parcelize
@Keep
data class BankFake(
    val id: String,
    val name: String
) : Parcelable


@Parcelize
@Keep
data class AepsUserInfo(
    @SerializedName("Namame")
    val name : String,
    @SerializedName("PandNo")
    val pan : String,
    @SerializedName("AaddNo")
    val aadhaar : String,
    @SerializedName("UserId")
    val userId : Int,
    @SerializedName("Addess")
    val address : String,
    @SerializedName("Staate")
    val state : String,
    @SerializedName("MobeNo")
    val mobileNumber : String,
    @SerializedName("Citity")
    val city : String,
    @SerializedName("OrgName")
    val organizationName : String,
    @SerializedName("Pinode")
    val pincode : String,
    @SerializedName("ekycstatus")
    val ekycStatus : String,
    @SerializedName("statss")
    val status : String,
    @SerializedName("kycstateid")
    val ekycStatusId : Int,
    @SerializedName("BankName")
    val bankName : String,
    @SerializedName("Aeps1id")
    val aepsMerchantId : String,
): Parcelable


@Parcelize
@Keep
data class AepsBankUserResponse(
    val aepsBankListResponse : AepsBankListResponse,
    val userInfo : AepsUserInfo
): Parcelable

@Parcelize
@Keep
data class MiniStatement(
    val date: String,
    val narration : String,
    val txnType : String,
    val amount: String
) : Parcelable
