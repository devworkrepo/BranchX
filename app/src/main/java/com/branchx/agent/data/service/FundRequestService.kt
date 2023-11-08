package com.branchx.agent.data.service

import com.branchx.agent.data.model.FundRequestBankResponse
import com.branchx.agent.data.model.FundRequestPaymentModeResponse
import com.branchx.agent.data.model.FundRequestUserTypeResponse
import com.branchx.agent.data.response.CommonResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface FundRequestService{

    @POST("FundReqPayMode")
    suspend fun fetchPaymentMode (@Body params : JsonObject ) : Response<FundRequestPaymentModeResponse>


    @POST("FundReqUserType")
    suspend fun fetchRequestUserType (@Body params : JsonObject ) : Response<FundRequestUserTypeResponse>


    @POST("FundReqBankName")
    suspend fun fetchBankList (@Body params : JsonObject ) : Response<FundRequestBankResponse>

    @POST("FundRequest")
    suspend fun makeRequest (@Body params : JsonObject ) : Response<CommonResponse>

}