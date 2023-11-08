package com.branchx.agent.data.repository

import com.branchx.agent.data.service.ReportService
import com.google.gson.JsonObject
import javax.inject.Inject

class ReportRepository @Inject constructor(private val reportService: ReportService) {

    suspend fun fetchDmtReport(data : JsonObject) = reportService.fetchReport(data)
    suspend fun fetchAepsReport(data : JsonObject) = reportService.fetchAepsReport(data)
    suspend fun fetchMatmReport(data : JsonObject) = reportService.fetchMatmReport(data)
    suspend fun fetchFundRequestReport(data : JsonObject) = reportService.fetchFundRequestReport(data)
    suspend fun fetchAepsLedgerReport(data : JsonObject) = reportService.fetchAepsLedgerReport(data)
    suspend fun fetchMatmLedgerReport(data : JsonObject) = reportService.fetchMatmLedgerReport(data)
    //suspend fun fetchPrepaidReports(data : JsonObject) = reportService.fetchP

}