package com.branchx.agent.data.model.support

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SupportContactResponse(
    @SerializedName("status")val status : Int,
    @SerializedName("message")val message : String?,
    @SerializedName("data")val contacts : List<SupportContact>?

) : Parcelable