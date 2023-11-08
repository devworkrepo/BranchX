package com.branchx.agent.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Balance(
    @SerializedName("MBalance")
    val mainBalance : String,
    @SerializedName("AepsBalance")
    val aepsBalance : String,
    @SerializedName("MATM")
    val matmBalance : String
):Parcelable