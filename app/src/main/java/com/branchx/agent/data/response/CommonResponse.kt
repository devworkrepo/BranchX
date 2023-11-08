package com.branchx.agent.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CommonResponse(val status: Int, val message: String, val transid: String? = null) :
    Parcelable