package com.branchx.agent.data.repository

import com.branchx.agent.data.service.RechargeBillService
import com.google.gson.JsonObject
import javax.inject.Inject

class RechargeBillRepository @Inject constructor(private val rechargeBillService: RechargeBillService) {

    //PROVIDER LISTING APIS
    suspend fun getCircleList(data: JsonObject) = rechargeBillService.getCircleList(data)
    suspend fun getCircleProvider(data: String) = rechargeBillService.getCircleProvider(data)
    suspend fun getPrepaidProviderList(data: JsonObject) = rechargeBillService.getPrepaidProviderList(data)
    suspend fun getPostpaidProviderList(data: JsonObject) = rechargeBillService.getPostpaidProviderList(data)
    suspend fun getDTHProviderList(data: JsonObject) = rechargeBillService.getDTHProviderList(data)
    suspend fun getGasProviderList(data: JsonObject) = rechargeBillService.getGasProviderList(data)
    suspend fun getWaterProviderList(data: JsonObject) = rechargeBillService.getWaterProviderList(data)
    suspend fun getBrodProviderList(data: JsonObject) = rechargeBillService.getBrodProviderList(data)
    suspend fun getLandProviderList(data: JsonObject) = rechargeBillService.getLandProviderList(data)


    suspend fun getElectricityProviderList(data: JsonObject) = rechargeBillService.getElectricityProviderList(data)

    //MAKE RECHARGE API
    suspend fun makeRecharge(data : JsonObject) = rechargeBillService.makeRecharge(data)

    //BILL PAY APIS

    suspend fun fetchBillInfo(data : JsonObject) = rechargeBillService.fetchBillInfo(data)
    suspend fun makeElectricityBillPayment(data : JsonObject) = rechargeBillService.makeElectricityBillPayment(data)
    suspend fun makeOtherBillPayment(data : JsonObject) = rechargeBillService.makeOtherBillPayment(data)
}