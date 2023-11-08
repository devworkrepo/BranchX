package com.branchx.agent.helper.api

import android.content.Context
import android.util.Log
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.response.GenerateTPinModel
import com.branchx.agent.data.response.TPinModel
import com.branchx.agent.helper.extns.toGsonJsonObject
import com.branchx.agent.helper.util.AESUtil
import com.branchx.agent.helper.util.AppUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class ApiData constructor(
    context: Context,
    private val appPreference: AppPreference,
) {

    var deviceId = ""//"dd84d142091aadcb"

    init {
        //TODO (uncomment dummy device id for go live)
        deviceId = AppUtil.getDeviceId(context)
    }

    fun data(dataMap: HashMap<String, String>? = null): JsonObject {


        return if (dataMap != null) {
            dataMap["loginid"] = appPreference.mobile
            dataMap["password"] = appPreference.password
            dataMap["deviceid"] = deviceId
            dataMap.toGsonJsonObject()
        } else {
            hashMapOf(
                "loginid" to appPreference.mobile,
                "password" to appPreference.password,
                "deviceid" to deviceId
            ).toGsonJsonObject()
        }
    }

    fun loginData(loginid: String, password: String, otp: String? = null): JsonObject {

        var otpData = ""
        otp?.let { otpData = it }

        return hashMapOf(
            "loginid" to loginid,
            "password" to password,
            "deviceid" to deviceId,
            "otp" to otpData
        ).toGsonJsonObject()
    }

    fun forgotPasswordData(loginid: String, deviceid: String): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId
        ).toGsonJsonObject()
    }

    fun neoData(loginid: String, password: String, mobile: String): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId,
            "password" to password,
            "mobile" to mobile
        ).toGsonJsonObject()
    }

    fun verifyNeoOtp(loginid: String, password: String, mobile: String, otp: String): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId,
            "password" to password,
            "mobile" to mobile,
            "otp" to otp
        ).toGsonJsonObject()
    }

    fun topUpWallet(
        loginid: String,
        password: String,
        tPin: String,
        mobileNumber: String,
        customerId: String,
        amount: String,
    ): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId,
            "password" to password,
            "tpin" to tPin,
            "mobile" to mobileNumber,
            "customerid" to customerId,
            "amount" to amount
        ).toGsonJsonObject()
    }

    fun getViewBalanceOtp(loginid: String, password: String, mobile: String): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId,
            "password" to password,
            "mobile" to mobile
        ).toGsonJsonObject()
    }

    fun verifyViewBalanceOtp(
        loginid: String,
        password: String,
        mobile: String,
        otp: String,
    ): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "deviceid" to deviceId,
            "password" to password,
            "mobile" to mobile,
            "otp" to otp
        ).toGsonJsonObject()
    }


    fun getTPinOtp(loginid: String, password: String): JsonObject {
        return hashMapOf(
            "loginid" to loginid,
            "password" to password,
            "deviceid" to deviceId
        ).toGsonJsonObject()
    }

    fun generateTPin(loginid: String, password: String, tPinModel: TPinModel): JsonObject {
        val generateTPinModel = GenerateTPinModel(tPinModel, deviceId, loginid, password)
        val jsonObject = JsonObject()
        val gsonStringData = Gson().toJsonTree(generateTPinModel).asJsonObject.toString()
        AppUtil.logger("BEFORE_ENCRYPTION:: $gsonStringData")
        jsonObject.addProperty("andrdenpdata", AESUtil.encryptData(gsonStringData))
        return jsonObject
    }
}
