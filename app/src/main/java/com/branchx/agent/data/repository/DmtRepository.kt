package com.branchx.agent.data.repository

import com.branchx.agent.data.service.DmtService
import com.google.gson.JsonObject
import javax.inject.Inject

class DmtRepository @Inject constructor(private val dmtService: DmtService) {
    suspend fun dmtOneSenderVerification(data: JsonObject) = dmtService.dmtOneSenderVerification(data)
    suspend fun dmtOneBeneficiaryList(data: JsonObject) = dmtService.dmtOneBeneficiaryList(data)
    suspend fun dmtOneDeleteBeneficiary(data: JsonObject) = dmtService.dmtOneDeleteBeneficiary(data)
    suspend fun dmtOneDeleteBeneficiaryOtp(data: JsonObject) = dmtService.dmtOneDeleteBeneficiaryOtp(data)
    suspend fun fetchBankList(data: JsonObject) = dmtService.fetchBankList(data)
    suspend fun fetchIfsc(bankName : String) = dmtService.fetchIfsc(bankName)
    suspend fun dmtOneAddBeneficiary(data: JsonObject) = dmtService.dmtOneAddBeneficiary(data)
    suspend fun dmtOneVerifyAddBeneficiary(data: JsonObject) = dmtService.dmtOneVerifyAddBeneficiary(data)
    suspend fun dmtOneRegisterSender(data: JsonObject) = dmtService.dmtOneRegisterSender(data)
    suspend fun dmtOneRegisterSenderOtp(data: JsonObject) = dmtService.dmtOneRegisterSenderOtp(data)
    suspend fun dmtOneRendOtp(data: JsonObject) = dmtService.dmtOneRendOtp(data)
    suspend fun dmtOneTransfer(data: JsonObject) = dmtService.dmtOneTransfer(data)
    suspend fun dmtOneBeneVerify(data: JsonObject) = dmtService.dmtOneAccountVerify(data)

}