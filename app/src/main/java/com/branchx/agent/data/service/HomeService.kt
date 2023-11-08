package com.branchx.agent.data.service

import com.google.gson.JsonObject
import com.branchx.agent.data.model.Balance
import com.branchx.agent.data.model.notification.NotificationResponse
import com.branchx.agent.data.model.support.SupportContactResponse
import com.branchx.agent.data.response.CommonResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface HomeService{

    @POST("GetBalance")
    suspend fun fetchBalance (@Query("loginid") loginId : String) : Response<Balance>

    @POST("Wall2Wall")
    suspend fun walletToWalletRequestOtp (@Body params : JsonObject) : Response<CommonResponse>


   @POST("InitiateR2r")
    suspend fun walletToWalletRequestVerify (@Body params : JsonObject) : Response<CommonResponse>
   @POST("Getnotification")
    suspend fun notification (@Body params : JsonObject) : Response<NotificationResponse>
   @POST("Supportcards")
    suspend fun supports (@Body params : JsonObject) : Response<SupportContactResponse>




}