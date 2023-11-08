package com.branchx.agent.data.service

import com.branchx.agent.data.model.settlement.SettlementReportResponse
import com.branchx.agent.data.model.settlement.SettlementRequestResponse
import com.branchx.agent.data.response.CommonResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface SettlementService{

    @POST("w2wRequestList")
    suspend fun fetchSettlementRequest (@Body params: JsonObject) : Response<SettlementRequestResponse>


    @POST("WalletTrans")
    suspend fun fetchSettlementReport (@Body params: JsonObject) : Response<SettlementReportResponse>

    @POST("Wallet2WalletnBank")
        suspend fun settlement (@Body params: JsonObject) : Response<CommonResponse>


}