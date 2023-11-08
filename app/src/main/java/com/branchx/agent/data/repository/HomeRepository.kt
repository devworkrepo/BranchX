package com.branchx.agent.data.repository

import com.google.gson.JsonObject
import com.branchx.agent.data.service.HomeService
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeService: HomeService) {

    //PROVIDER LISTING APIS
    suspend fun fetchBalance(loginId: String) = homeService.fetchBalance(loginId)
    suspend fun walletToWalletRequestOtp(data: JsonObject) = homeService.walletToWalletRequestOtp(data)
    suspend fun walletToWalletRequestVerify(data: JsonObject) = homeService.walletToWalletRequestVerify(data)
    suspend fun notification(data: JsonObject) = homeService.notification(data)
    suspend fun supports(data: JsonObject) = homeService.supports(data)

}