package com.branchx.agent.data.repository

import com.branchx.agent.data.service.MatmService
import com.google.gson.JsonObject
import javax.inject.Inject

class MatmRepository @Inject constructor(private val matmService: MatmService) {

    //PROVIDER LISTING APIS
    suspend fun requestMatmData(data: JsonObject) = matmService.requestMatmData(data)
    suspend fun updateMatmData(data: JsonObject) = matmService.updateMatmData(data)
}