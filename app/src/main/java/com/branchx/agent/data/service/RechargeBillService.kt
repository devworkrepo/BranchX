package com.branchx.agent.data.service

import com.branchx.agent.data.response.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RechargeBillService {

    @POST("GetElecCircle")
    suspend fun getCircleList(@Body param: JsonObject): Response<CircleListResponse>

    @POST("GetElecProonCircle")
    suspend fun getCircleProvider(@Query("CircleID") circleid: String): Response<ProviderResponse>

    //PROVIDER LISTING APIS
    @POST("GetPrepaidProvider")
    suspend fun getPrepaidProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetPostpaidProvider")
    suspend fun getPostpaidProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetDTHProvider")
    suspend fun getDTHProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetGasProvider")
    suspend fun getGasProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetWaterProvider")
    suspend fun getWaterProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetBrodProvider")
    suspend fun getBrodProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetLandProvider")
    suspend fun getLandProviderList(@Body params: JsonObject): Response<ProviderResponse>

    @POST("GetElectricityProvider")
    suspend fun getElectricityProviderList(@Body params: JsonObject): Response<ProviderResponse>

    //RECHARGE API
    @POST("Recharge")
    suspend fun makeRecharge(@Body data: JsonObject): Response<CommonResponse>

    //BILL PAY APIS
    @POST("BillFetch")
    suspend fun fetchBillInfo(@Body data: JsonObject): Response<FetchBillResponse>

    @POST("BillPayment")
    suspend fun makeElectricityBillPayment(@Body data: JsonObject): Response<BillPaymentResponse>


    @POST("PrTrans")
    suspend fun makeOtherBillPayment(@Body data: JsonObject): Response<BillPaymentResponse>
}