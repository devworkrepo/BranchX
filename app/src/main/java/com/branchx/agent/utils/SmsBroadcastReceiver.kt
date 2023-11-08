package com.branchx.agent.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.regex.Pattern
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {
    private var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? =
        null

    fun setUpOtpListener(smsBroadcastReceiverListener: SmsBroadcastReceiverListener) {
        this.smsBroadcastReceiverListener = smsBroadcastReceiverListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val bundle = intent.extras
            if (bundle != null) {
                val status: Status? = bundle[SmsRetriever.EXTRA_STATUS] as Status?
                if (status != null) {
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = bundle[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                            if (message != null) {
                                val pattern = Pattern.compile("\\d{6}")
                                val matcher = pattern.matcher(message)
                                if (matcher.find()) {
                                    val myOtp = matcher.group(0)
                                    smsBroadcastReceiverListener?.onOTPSuccess(myOtp)
                                }
                            }
                        }

                        CommonStatusCodes.TIMEOUT -> smsBroadcastReceiverListener?.onOTPTimeOut()
                    }
                }
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onOTPSuccess(otp: String)
        fun onOTPTimeOut()
    }
}