package com.branchx.agent.data.service

import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.data.model.SenderInfoDmtTwo
import com.branchx.agent.data.response.BeneficiaryListResponse
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.data.response.DmtTransactionResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DmtTwoService {

    @POST("Dmt2vr")
    suspend fun senderVerification (@Body params : JsonObject) : Response<SenderInfo>
    @POST("Dmt2ROTP")
    suspend fun registerSender (@Body params : JsonObject) : Response<CommonResponse>
    @POST("Dmt2rAdd")
    suspend fun registerSenderVerify (@Body params : JsonObject) : Response<CommonResponse>
    @POST("Dmt2GetLimitR")
    suspend fun fetchSenderData (@Body params : JsonObject) : Response<SenderInfoDmtTwo>

    @POST("getdmt2BeneList")
    suspend fun fetchBeneficiaryList (@Body params : JsonObject) : Response<BeneficiaryListResponse>
    @POST("Dmt2VfyBene")
    suspend fun verifyBeneficiary (@Body params : JsonObject) : Response<CommonResponse>
    @POST("DMT2BeneficiaryDel")
    suspend fun deleteBeneficiary (@Body params : JsonObject) : Response<CommonResponse>
    @POST("Dmt2AddBene")
    suspend fun addBeneficiary (@Body params : JsonObject) : Response<CommonResponse>
    @POST("dmt2AddnVfyBene")
    suspend fun verifyAddBeneficiary (@Body params : JsonObject) : Response<CommonResponse>
    @POST("dm2Transactions")
    suspend fun transfer (@Body params : JsonObject) : Response<DmtTransactionResponse>


}