package com.branchx.agent.helper.extns

import com.branchx.agent.ui.fragment.main.ServiceType

fun ServiceType.strName() = when (this) {
        ServiceType.PREPAID -> "Prepaid"
        ServiceType.POSTPAID -> "Postpaid"
        ServiceType.DTH -> "DTH"
        ServiceType.WATER -> "Water"
        ServiceType.GAS -> "Gas"
        ServiceType.BROADBAND -> "Broadband"
        ServiceType.LANDLINE -> "LandLine"
        ServiceType.ELECTRICITY -> "Electricity"
        else -> "NA"
    }
