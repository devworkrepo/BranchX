package com.branchx.agent.data.repository

import com.branchx.agent.data.service.SettlementService
import com.google.gson.JsonObject
import javax.inject.Inject

class SettlementRepository @Inject constructor(private val settlementService: SettlementService) {

    suspend fun fetchSettlementRequest(data : JsonObject) = settlementService.fetchSettlementRequest(data)
    suspend fun fetchWalletTransaction(data : JsonObject) = settlementService.fetchSettlementReport(data)
    suspend fun settlement(data : JsonObject) = settlementService.settlement(data)

}