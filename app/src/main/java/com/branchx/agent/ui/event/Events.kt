package com.branchx.agent.ui.event

import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse

object Events {
    data class MobileNumberEvent(var mobileNumber: String?)
    data class NeoBankingVerifyOtpResponseEvent(var neoBankingVerifyOtpResponse: NeoBankingVerifyOtpResponse?)
}