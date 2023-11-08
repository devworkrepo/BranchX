package com.branchx.agent.data.service

import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.data.response.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface DmtService {

    //SENDER VERIFICATION
    @POST("Dmt2vr")
    suspend fun dmtOneSenderVerification (@Body params : JsonObject) : Response<SenderInfo>

    @POST("getdmt2BeneList")
    suspend fun dmtOneBeneficiaryList (@Body params : JsonObject) : Response<DmtOneBeneficiaryListResponse>

    @POST("DMT2BeneficiaryDel")
    suspend fun dmtOneDeleteBeneficiary (@Body params : JsonObject) : Response<CommonResponse>
    @POST("Dmt2DelBeneotp")
    suspend fun dmtOneDeleteBeneficiaryOtp (@Body params : JsonObject) : Response<CommonResponse>
//DELETE dmtOneDeleteBeneficiaryOtp
    @POST("GetBankList")
    suspend fun fetchBankList (@Body params : JsonObject) : Response<DmtBankListResponse>

    @POST("Dmt2AddBene")
    suspend fun dmtOneAddBeneficiary (@Body params : JsonObject) : Response<CommonResponse>


    @POST("Dmt2AddVfyBene")
    suspend fun dmtOneVerifyAddBeneficiary (@Body params : JsonObject) : Response<CommonResponse>

    @POST("Dmt2rAdd")
    suspend fun dmtOneRegisterSender (@Body params : JsonObject) : Response<CommonResponse>

    @POST("Dmt2ROTP")
    suspend fun dmtOneRegisterSenderOtp (@Body params : JsonObject) : Response<CommonResponse>

    @POST("Dmt1RemOTP")
    suspend fun dmtOneRendOtp (@Body params : JsonObject) : Response<CommonResponse>
//RemoOTP is not functional
    @POST("Dmt2VfyBene")
    suspend fun dmtOneAccountVerify (@Body params : JsonObject) : Response<CommonResponse>

    @POST("dm2Transactions")
    suspend fun dmtOneTransfer (@Body params : JsonObject) : Response<DmtTransactionResponse>


    @POST("getifscc")
    suspend fun fetchIfsc (
        @Query("bankname") bankName : String
    ) : Response<IfscResponse>

}