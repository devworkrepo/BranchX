package com.branchx.agent.data.service

import com.branchx.agent.data.response.NeoBankingResponse
import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse
import com.branchx.agent.data.response.TPinResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NeoBankingService {

    @POST("VerifyUser")
    suspend fun getNeoOtp(@Body params: JsonObject): Response<NeoBankingResponse>

    @POST("VerifyUserOTP")
    suspend fun verifyNeoOtp(@Body params: JsonObject): Response<NeoBankingVerifyOtpResponse>

    @POST("TopupUserValidate")
    suspend fun topUpWallet(@Body params: JsonObject): Response<NeoBankingResponse>

    @POST("VerifyUserWallet")
    suspend fun getViewBalanceOtp(@Body params: JsonObject): Response<NeoBankingResponse>

    @POST("VerifyUserOTP")
    suspend fun verifyViewBalanceOtp(@Body params: JsonObject): Response<NeoBankingVerifyOtpResponse>



    //TPin
    @POST("GenerateTpinOTP")
    suspend fun generateTPinOTP(@Body params: JsonObject): Response<TPinResponse>

    @POST("GenerateTpin")
    suspend fun generateTPin(@Body params: JsonObject): Response<TPinResponse>
}