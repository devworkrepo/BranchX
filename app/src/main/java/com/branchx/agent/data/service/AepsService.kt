package com.branchx.agent.data.service

import com.branchx.agent.data.model.AepsBankListResponse
import com.branchx.agent.data.model.AepsTransactionResponse
import com.branchx.agent.data.model.AepsUserInfo
import com.branchx.agent.data.response.CommonResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AepsService {

    @POST("EkycBankList")
    suspend fun bankList(@Body params: JsonObject): Response<AepsBankListResponse>
    @POST("Aepstwoauth")
    suspend fun f2fAuth(@Body params: JsonObject): Response<CommonResponse>
    @POST("UserAEPSTrans")
    suspend fun aepsTransaction(@Body params: JsonObject): Response<AepsTransactionResponse>

    @POST("UserAadhaarPayTrans")
    suspend fun aadhaarTransaction(@Body params: JsonObject): Response<AepsTransactionResponse>

    @POST("EkycUserdata")
    suspend fun aepsUserData(@Body params: JsonObject): Response<AepsUserInfo>

    @POST("Ekyc1onboard")
    suspend fun aepsOnboard(@Body params: JsonObject): Response<CommonResponse>

    @POST("Verifydevice")
    suspend fun verifyDevice(@Body params: JsonObject): Response<CommonResponse>

    @POST("Validateverifydevice")
    suspend fun validateVerifyDevice(@Body params: JsonObject): Response<CommonResponse>
    @POST("EkycOn")
    suspend fun authKyc(@Body params: JsonObject): Response<CommonResponse>


}
