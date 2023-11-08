package com.branchx.agent.data.service

import com.branchx.agent.data.model.report.AepsReportResponse
import com.branchx.agent.data.model.FundRequestReportResponse
import com.branchx.agent.data.model.DmtReportResponse
import com.branchx.agent.data.model.LedgerReportResponse
import com.branchx.agent.data.model.report.MatmReportResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ReportService{

    @POST("getTransList")
    suspend fun fetchReport (@Body params: JsonObject) : Response<DmtReportResponse>


    @POST("getAepsTransList")
    suspend fun fetchAepsReport (@Body params: JsonObject) : Response<AepsReportResponse>


    @POST("getmatmTransList")
    suspend fun fetchMatmReport (@Body params: JsonObject) : Response<MatmReportResponse>

    @POST("GetFundRequestList")
    suspend fun fetchFundRequestReport (@Body params: JsonObject) : Response<FundRequestReportResponse>


    @POST("aepsWalletTrans")
    suspend fun fetchAepsLedgerReport (@Body params: JsonObject) : Response<LedgerReportResponse>


    @POST("MatmWalletTrans")
    suspend fun fetchMatmLedgerReport (@Body params: JsonObject) : Response<LedgerReportResponse>


}