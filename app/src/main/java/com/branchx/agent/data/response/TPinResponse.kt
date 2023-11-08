package com.branchx.agent.data.response

import com.google.gson.annotations.SerializedName

data class TPinResponse(

    @SerializedName("status")
    var status: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("errorcode")
    var errorCode: Int,
)


data class GenerateTPinModel(
    @SerializedName("GenTPin")
    var GenTPin: TPinModel,

    @SerializedName("deviceid")
    var deviceid: String,

    @SerializedName("loginid")
    var loginid: String,

    @SerializedName("password")
    var password: String
)

data class TPinModel(

    @SerializedName("GenTpinNew")
    var newTPin: String,

    @SerializedName("GenTpinCon")
    var confirmTPin: String,

    @SerializedName("GenTpinOtp")
    var tPinOtp: String,
)
