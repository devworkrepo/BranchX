package com.branchx.agent.data.repository

import com.branchx.agent.data.service.DmtService
import com.branchx.agent.data.service.DmtTwoService
import com.google.gson.JsonObject
import javax.inject.Inject

class DmtTwoRepository @Inject constructor(private val service: DmtTwoService) {

    suspend fun senderVerification(data: JsonObject) = service.senderVerification(data)
    suspend fun registerSender(data: JsonObject) = service.registerSender(data)
    suspend fun registerSenderVerify(data: JsonObject) = service.registerSenderVerify(data)
    suspend fun fetchSenderData(data: JsonObject) = service.fetchSenderData(data)
    suspend fun fetchBeneficiaryList(data: JsonObject) = service.fetchBeneficiaryList(data)
    suspend fun verifyBeneficiary(data: JsonObject) = service.verifyBeneficiary(data)
    suspend fun deleteBeneficiary(data: JsonObject) = service.deleteBeneficiary(data)
    suspend fun addBeneficiary(data: JsonObject) = service.addBeneficiary(data)
    suspend fun verifyAddBeneficiary(data: JsonObject) = service.verifyAddBeneficiary(data)
    suspend fun transfer(data: JsonObject) = service.transfer(data)
}