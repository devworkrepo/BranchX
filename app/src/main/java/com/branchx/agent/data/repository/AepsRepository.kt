package com.branchx.agent.data.repository

import com.branchx.agent.data.service.AepsService
import com.google.gson.JsonObject
import javax.inject.Inject

class AepsRepository @Inject constructor(private val aepsService: AepsService) {
    suspend fun bankList(data: JsonObject) = aepsService.bankList(data)
    suspend fun aepsTransaction(data: JsonObject) = aepsService.aepsTransaction(data)

    suspend fun f2fAuth(data: JsonObject) = aepsService.f2fAuth(data)
    suspend fun aadhaarPayTransaction(data: JsonObject) = aepsService.aadhaarTransaction(data)
    suspend fun aepsUserData(data: JsonObject) = aepsService.aepsUserData(data)

    //aeps kyc
    suspend fun aepsOnboard(data: JsonObject) = aepsService.aepsOnboard(data)
    suspend fun verifyDevice(data: JsonObject) = aepsService.verifyDevice(data)
    suspend fun validateVerifyDevice(data: JsonObject) = aepsService.validateVerifyDevice(data)
    suspend fun authKyc(data: JsonObject) = aepsService.authKyc(data)
}