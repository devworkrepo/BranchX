package com.branchx.agent.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.data.model.DmtBank
import com.branchx.agent.data.model.DmtTransaction
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize




data class DmtBankListResponse(
    val count: Int,
    @SerializedName("data")
    val bank: List<DmtBank>,
    val message: String,
    val status: Int
)

data class BeneficiaryListResponse(
    val count: Int,
    @SerializedName("data")
    val beneficiary: List<BeneficiaryInfo>,
    val message: String,
    val status: Int
)


data class DmtOneBeneficiaryListResponse(
    val count: Int,
    @SerializedName("data")
    val dmtOneBeneficiary: List<BeneficiaryInfo>,
    val message: String,
    val status: Int
)

@Keep
@Parcelize
data class DmtTransactionResponse(
    val status : Int=0,
    val message: String="",

    @SerializedName("acno")
    val accountNumber: String = "",

    @SerializedName("agentid")
    val agentId: String="",

    @SerializedName("bank")
    val bankName: String="",

    val date: String="",

    @SerializedName("details")
    val dmtTransactionList: List<DmtTransaction> = listOf(),

    @SerializedName("prname")
    val dmtType: String = "",

    @SerializedName("remitterno")
    val remitterNumber: String=""
) : Parcelable



data class IfscResponse(
    val masterifsccode : String
)