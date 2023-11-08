package com.branchx.agent.data.repository

import com.branchx.agent.data.service.NeoBankingService
import com.google.gson.JsonObject
import javax.inject.Inject

class NeoBankingRepository @Inject constructor(private val neoBankingService: NeoBankingService) {
    suspend fun getNeoOTP(data: JsonObject) = neoBankingService.getNeoOtp(data)
    suspend fun verifyNeoOtp(data: JsonObject) = neoBankingService.verifyNeoOtp(data)
    suspend fun topUpWallet(data: JsonObject) = neoBankingService.topUpWallet(data)
    suspend fun getViewBalanceOtp(data: JsonObject) = neoBankingService.getViewBalanceOtp(data)
    suspend fun verifyViewBalanceOtp(data: JsonObject) = neoBankingService.verifyViewBalanceOtp(data)

    //TPin
    suspend fun generateTPinOTP(data: JsonObject) = neoBankingService.generateTPinOTP(data)
    suspend fun generateTPin(data: JsonObject) = neoBankingService.generateTPin(data)
}