package com.branchx.agent.data.service

import com.branchx.agent.data.response.*
import com.fingpay.microatmsdk.data.StatusReqModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("AppLogin")
    suspend fun appLogin(@Body params: JsonObject): Response<LoginResponse>

    @POST("AppChangePass")
    suspend fun changePassword(@Body params: JsonObject): Response<CommonResponse>

    @POST("Forgotpassword")
    suspend fun forgotPassword(@Body params: JsonObject): Response<CommonResponse>

    @POST("AppGenerateOtp")
    suspend fun generateOtp(@Body params: JsonObject): Response<AppGenerateOtp>

    @POST("AppDeviceVerfiy")
    suspend fun verifyDeviceOtp(@Body params: JsonObject): Response<CommonResponse>

    @POST("AppOtpVerfiy")
    suspend fun verifyLoginOtp(@Body params: JsonObject): Response<CommonResponse>

    //registration
    @FormUrlEncoded
    @POST("GetState")
    suspend fun fetchStateList(@Field("no-param") no_param: String = "no-param"): Response<StateListResponse>

    @FormUrlEncoded
    @POST("GetCities")
    suspend fun fetchCityList(@Field("no-param") no_param: String = "no-param"): Response<CityListResponse>

    @POST("UserRegistration")
    suspend fun registerUser(@Body params: JsonObject): Response<CommonResponse>

    @POST("GetUserList")
    suspend fun userList(@Body params: JsonObject): Response<CommonResponse>

}
