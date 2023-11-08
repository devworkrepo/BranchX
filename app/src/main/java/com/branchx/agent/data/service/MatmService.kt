package com.branchx.agent.data.service

import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.data.response.MatmRequestResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface MatmService{

    @POST("UserMatmTrans")
    suspend fun requestMatmData (@Body params : JsonObject) : Response<MatmRequestResponse>


    @POST("MatmFakeApi2")
    suspend fun updateMatmData (@Body params : JsonObject) : Response<CommonResponse>


}