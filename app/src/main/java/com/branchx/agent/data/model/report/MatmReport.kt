package com.branchx.agent.data.model.report
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MatmReportResponse (

    @SerializedName("status"  ) var status  : Int?            = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("count"   ) var count   : Int?            = null,
    @SerializedName("data"    ) var report    : ArrayList<MatmReport>? = null

) : Parcelable

@Parcelize
@Keep
data class MatmReport (

    @SerializedName("Pansid"    ) var id    : String? = null,
    @SerializedName("PantID"    ) var serviceName    : String? = null,
    @SerializedName("Amount"    ) var amount    : Int?    = null,
    @SerializedName("Panate"    ) var date    : String? = null,
    @SerializedName("Oprtus"    ) var statusId    : String? = null,
    @SerializedName("OprStatus" ) var status : String? = null,
    @SerializedName("Ttype"     ) var transactionType     : String? = null,
    @SerializedName("OprrID"    ) var operatorId    : String? = null,
    @SerializedName("ConsNo"    ) var customerNumber    : String? = null,
    @SerializedName("ConsNm"    ) var customerName    : String? = null,
    @SerializedName("BankID"    ) var bankName    : String? = null,
    @SerializedName("Apimsg"    ) var message    : String? = null,
    @SerializedName("cardno"    ) var cardNumber    : String? = null,
    @SerializedName("lgdrsts"   ) var ledgerStatus   : String? = null,
    var isContentInfoVisible : Boolean = false

) : Parcelable