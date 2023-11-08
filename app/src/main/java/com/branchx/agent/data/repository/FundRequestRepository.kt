package com.branchx.agent.data.repository

import com.branchx.agent.data.service.FundRequestService
import com.google.gson.JsonObject
import javax.inject.Inject

class FundRequestRepository @Inject constructor(private val fundRequestService: FundRequestService) {

    suspend fun fetchPaymentMode(data: JsonObject) = fundRequestService.fetchPaymentMode(data)
    suspend fun fetchRequestUserType(data: JsonObject) = fundRequestService.fetchRequestUserType(data)
    suspend fun fetchBankList(data: JsonObject) = fundRequestService.fetchBankList(data)
    suspend fun makeRequest(data: JsonObject) = fundRequestService.makeRequest(data)
}