package com.branchx.agent.helper.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.branchx.agent.BuildConfig
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.regex.Pattern

object AppUtil {

    fun logger(value: String) {
        if (BuildConfig.DEBUG)
            Log.d("AppLogger", "value : $value")
    }

    fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceId(context: Context?): String {
        var imei: String? = null
        try {
            val telephonyManager =
                context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            imei = Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            imei = null
        }
        return imei ?: ""
    }

    fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$"
        );
        return passwordREGEX.matcher(password).matches()
    }

}


/*alias*/
