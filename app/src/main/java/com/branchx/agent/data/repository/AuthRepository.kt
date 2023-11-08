package com.branchx.agent.data.repository

import com.branchx.agent.data.service.AuthService
import com.google.gson.JsonObject
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService) {
    suspend fun appLogin(data: JsonObject) = authService.appLogin(data)
    suspend fun changePassword(data: JsonObject) = authService.changePassword(data)
    suspend fun forgotPassword(data: JsonObject) = authService.forgotPassword(data)
    suspend fun generateOtp(data: JsonObject) = authService.generateOtp(data)
    suspend fun verifyDeviceOtp(data: JsonObject) = authService.verifyDeviceOtp(data)
    suspend fun verifyLoginOtp(data: JsonObject) = authService.verifyDeviceOtp(data)

    //registration
    suspend fun fetchStateList(countryId: String) = authService.fetchStateList(countryId)
    suspend fun fetchCityList() = authService.fetchCityList()
    suspend fun registerUser(andrdenpdata: JsonObject) = authService.registerUser(andrdenpdata)
    suspend fun userList(andrdenpdata: JsonObject) = authService.userList(andrdenpdata)
}