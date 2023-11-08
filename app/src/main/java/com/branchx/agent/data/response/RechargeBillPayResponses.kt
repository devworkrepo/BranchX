package com.branchx.agent.data.response

import android.os.Parcelable
import com.branchx.agent.data.model.Provider
import com.branchx.agent.helper.util.AppConstants
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CircleListResponse(
    val status : Int,
    val message: String,
    val count: Int,
    @SerializedName("data")
    val circleList : List<Circle>
):Parcelable

@Parcelize
data class Circle(
    val id : Int,
    @SerializedName("StateName")
    val stateName : String
):Parcelable


data class ProviderResponse(
    val count: Int,
    @SerializedName("data")
    val providers: List<Provider>,
    val message: String,
    val status: Int
)


data class FetchBillResponse(
    val billNumber: String?,
    val billerName: String?,
    val dueAmount: String?,
    val dueDate: String?,
    val message: String,
    val status: Int,
    val eRoNoTF : Int=0,
)


@Parcelize
data class BillPaymentResponse(
    @SerializedName("datetime")
    val date: String = AppConstants.EMPTY,
    @SerializedName("ec")
    val ec: Int=0,
    @SerializedName("operatorid")
    val operatorId: String= AppConstants.EMPTY,
    @SerializedName("transactionid")
    val transactionId: String= AppConstants.EMPTY,
    val message: String= AppConstants.EMPTY,
    val status: Int =3,
):Parcelable