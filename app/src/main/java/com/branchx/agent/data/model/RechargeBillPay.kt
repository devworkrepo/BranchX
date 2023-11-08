package com.branchx.agent.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Provider(
    @SerializedName("Proame")
    val providerName: String,
    @SerializedName("ids")
    val id: Int
) : Parcelable{
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}