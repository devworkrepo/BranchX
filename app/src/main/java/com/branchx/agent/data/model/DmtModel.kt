package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DmtBank(
    @SerializedName("BankName")
    val bankName: String,
    @SerializedName("indexing")
    val bankId: Int=1
)

@Keep
@Parcelize
data class BeneficiaryInfo(
    @SerializedName("BenFSC")
    val ifsc: String,
    @SerializedName("Bename")
    val name: String,
    @SerializedName("Benber")
    val Benber: String,
    @SerializedName("Benbnk")
    val bankName: String,
    @SerializedName("BeneNo")
    val mobileNumber: String,
    @SerializedName("BenrId")
    val beneficiaryId: String?,
    @SerializedName("BeneVrfy")
    val BeneVrfy: String?,
    val id: String
):Parcelable

@Parcelize
data class SenderInfo(
    @SerializedName("fname")
    var firstName: String?,
    @SerializedName("lname")
    var lastName: String?,
    var name: String?,
    var message: String,
    @SerializedName("mobileno")
    var mobileNumber: String?,
    @SerializedName("rem_bal")
    var remainingBalance: String?,
    var status: Int,
    var verify: String?
) : Parcelable {
    fun getBeneficiaryName(): String {
        return name ?: "$firstName $lastName"
    }
}


@Parcelize
data class SenderInfoDmtTwo(
    val status: Int,
    val message: String,
    @SerializedName("monthlylimit")
    val monthlyLimit: String? = null,
    var name: String? = null
) : Parcelable


@Keep
@Parcelize
data class DmtTransaction(
    @SerializedName("Charges")
    val charges: String,
    @SerializedName("GST")
    val gst: String,
    @SerializedName("refno")
    val refNumber: String,
    val remarks: String,
    @SerializedName("rrnno")
    val rrNumber: String,
    val status: Int,
    @SerializedName("txnamt")
    val transactionAmount: String,
    val message: String ,
    val date : String
):Parcelable