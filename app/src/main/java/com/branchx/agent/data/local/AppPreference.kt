package com.branchx.agent.data.local

import android.content.SharedPreferences
import com.branchx.agent.data.model.User
import com.branchx.agent.data.response.MatmPrefDetail
import com.branchx.agent.helper.util.AppConstants
import com.google.gson.Gson
import javax.inject.Inject

class AppPreference @Inject constructor(private val sharedPreferences: SharedPreferences) {


    var mobile: String
        get() = getString(MOBILE)
        set(value) = setString(value, MOBILE)
    var password: String
        get() = getString(PASSWORD)
        set(value) = setString(value, PASSWORD)
    var loginCount: Int
        get() = getInteger(LOGIN_COUNT, 2)
        set(value) = setInteger(value, LOGIN_COUNT)
    var isApiTransaction: Boolean
        get() = getBoolean(IS_TRANSACTION_API)
        set(value) = setBoolean(value, IS_TRANSACTION_API)
    var isLoginCheck: Boolean
        get() = getBoolean(IS_LOGIN_CHECK)
        set(value) = setBoolean(value, IS_LOGIN_CHECK)

    var selectRdDevice: String
        get() {
            val selectedDevice = getString(SELECT_RD_DEVICE)
            return if (selectedDevice.isNotEmpty()) selectedDevice
            else AppConstants.MANTRA
        }
        set(value) = setString(value, SELECT_RD_DEVICE)

    var user: User
        get() {
            val strUser = getString(USER)
            return Gson().fromJson(strUser, User::class.java)
        }
        set(value) {
            val strUser = Gson().toJson(value).toString()
            setString(strUser, USER)
        }
    var matmDetail: MatmPrefDetail
        set(value) = setString(Gson().toJson(value).toString(), MAMT_DETAIL)
        get() {
            val strDetail: String = getString(MAMT_DETAIL)
            return if (strDetail.isEmpty()) MatmPrefDetail()
            else Gson().fromJson(strDetail, MatmPrefDetail::class.java)
        }


    //STRING DATA
    private fun setString(value: String, tag: String) =
        sharedPreferences.edit().putString(tag, value).apply()

    private fun getString(tag: String) =
        sharedPreferences.getString(tag, AppConstants.EMPTY) ?: ""

    //BOOLEAN DATA
    private fun setBoolean(value: Boolean, tag: String) =
        sharedPreferences.edit().putBoolean(tag, value).apply()

    private fun getBoolean(tag: String) =
        sharedPreferences.getBoolean(tag, false)

    //INTEGER DATA
    private fun setInteger(value: Int, tag: String) =
        sharedPreferences.edit().putInt(tag, value).apply()

    private fun getInteger(tag: String, defaultValue: Int = 0) =
        sharedPreferences.getInt(tag, defaultValue)

    //TAGS
    companion object {
        const val USER = "user"
        const val MOBILE = "mobile"
        const val PASSWORD = "password"
        const val LOGIN_COUNT = "login_count"
        const val IS_LOGIN_CHECK = "is_login_check"
        const val IS_TRANSACTION_API = "is_transaction_api"
        const val MAMT_DETAIL = "matm_detail"
        const val SELECT_RD_DEVICE = "selected_rd_device"
    }

    fun clearDataOnLogout() {
        this.mobile = ""
        this.password = ""

    }

}