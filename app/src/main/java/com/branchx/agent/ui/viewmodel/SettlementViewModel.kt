package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.settlement.SettlementReportResponse
import com.branchx.agent.data.model.settlement.SettlementRequestResponse
import com.branchx.agent.data.repository.SettlementRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.SingleMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettlementViewModel @Inject constructor(
    private val settlementRepository: SettlementRepository,
    private val apiData: ApiData
) : BaseViewModel() {

    var  fromDate: String = ""
    var  toDate: String = ""

    //new settlement
    var walletType : String = "Aeps1"
    var transferType : String = "BD"
    var amount : String = ""
    var mType = "IMPS"


    private val _fetchSettlementRequestObs = MutableLiveData<Resource<SettlementRequestResponse>>()
    val fetchSettlementRequestObs: LiveData<Resource<SettlementRequestResponse>> = _fetchSettlementRequestObs

    fun fetchSettlementRequest() {
        viewModelScope.launch {
            try {
                _fetchSettlementRequestObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    settlementRepository.fetchSettlementRequest(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                        "reclimit" to "100",
                    )))
                }.body()!!
                _fetchSettlementRequestObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchSettlementRequestObs.value = Resource.Failure(e)
            }
        }
    }

    private val _fetchSettlementReportObs = MutableLiveData<Resource<SettlementReportResponse>>()
    val fetchSettlementReportObs: LiveData<Resource<SettlementReportResponse>> = _fetchSettlementReportObs

    fun fetchSettlementReport() {
        viewModelScope.launch {
            try {
                _fetchSettlementReportObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    settlementRepository.fetchWalletTransaction(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                        "reclimit" to "100",
                    )))
                }.body()!!
                _fetchSettlementReportObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchSettlementReportObs.value = Resource.Failure(e)
            }
        }
    }

    private val _onSettlementObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val onSettlementObs: LiveData<Resource<CommonResponse>> = _onSettlementObs

    fun proceedSettlementRequest(){

        viewModelScope.launch {
            try {
                _onSettlementObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    settlementRepository.settlement(apiData.data(hashMapOf(
                        "amt" to amount,
                        "wallettype" to walletType,
                        "trantype" to transferType,
                    )))
                }.body()!!
                _onSettlementObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onSettlementObs.value = Resource.Failure(e)
            }
        }

    }



}