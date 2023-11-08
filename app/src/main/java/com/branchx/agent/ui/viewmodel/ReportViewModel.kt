package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.report.AepsReportResponse
import com.branchx.agent.data.model.FundRequestReportResponse
import com.branchx.agent.data.model.DmtReportResponse
import com.branchx.agent.data.model.LedgerReportResponse
import com.branchx.agent.data.model.report.MatmReportResponse
import com.branchx.agent.data.repository.ReportRepository
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val apiData: ApiData
) : BaseViewModel() {

    var  fromDate: String = ""
    var  toDate: String = ""


    private val _fetchDmtReportObs = MutableLiveData<Resource<DmtReportResponse>>()
    val fetchDmtDmtReportObs: LiveData<Resource<DmtReportResponse>> = _fetchDmtReportObs

    fun fetchDmtReport() {
        viewModelScope.launch {
            try {
                _fetchDmtReportObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    reportRepository.fetchDmtReport(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                        "reclimit" to "100",
                    )))
                }.body()!!
                _fetchDmtReportObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchDmtReportObs.value = Resource.Failure(e)
            }
        }
    }

    private val _fetchAepsReportObs = MutableLiveData<Resource<AepsReportResponse>>()
    val fetchAepsDmtReportObs: LiveData<Resource<AepsReportResponse>> = _fetchAepsReportObs

    fun fetchAepsReport() {
        viewModelScope.launch {
            try {
                _fetchAepsReportObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    reportRepository.fetchAepsReport(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                        "reclimit" to "100",
                    )))
                }.body()!!
                _fetchAepsReportObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchAepsReportObs.value = Resource.Failure(e)
            }
        }
    }


    private val _fetchMatmReportObs = MutableLiveData<Resource<MatmReportResponse>>()
    val fetchMatmReportObs: LiveData<Resource<MatmReportResponse>> = _fetchMatmReportObs

    fun fetchMatmReport() {
        viewModelScope.launch {
            try {
                _fetchMatmReportObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    reportRepository.fetchMatmReport(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                        "reclimit" to "100",
                    )))
                }.body()!!
                _fetchMatmReportObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchMatmReportObs.value = Resource.Failure(e)
            }
        }
    }





    private val _fundRequestReportObs = MutableLiveData<Resource<FundRequestReportResponse>>()
    val fundRequestReportObs: LiveData<Resource<FundRequestReportResponse>> = _fundRequestReportObs

    fun fetchFundRequestReports() {
        viewModelScope.launch {
            try {
                _fundRequestReportObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){
                    reportRepository.fetchFundRequestReport(apiData.data(hashMapOf(
                        "frmdate" to fromDate,
                        "todate" to toDate,
                    )))
                }.body()!!
                _fundRequestReportObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _fundRequestReportObs.value = Resource.Failure(e)
            }
        }
    }


    private val _ledgerReportResponseObs = MutableLiveData<Resource<LedgerReportResponse>>()
    val ledgerReportResponseObs: LiveData<Resource<LedgerReportResponse>> = _ledgerReportResponseObs

    fun fetchLedgerReports(key : Int) {
        viewModelScope.launch {
            try {
                _ledgerReportResponseObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO){

                    if(key == 0){
                        reportRepository.fetchAepsLedgerReport(apiData.data(hashMapOf(
                            "frmdate" to fromDate,
                            "todate" to toDate,
                        )))
                    }
                    else {
                        reportRepository.fetchMatmLedgerReport(apiData.data(hashMapOf(
                            "frmdate" to fromDate,
                            "todate" to toDate,
                        )))
                    }
                }.body()!!
                _ledgerReportResponseObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _ledgerReportResponseObs.value = Resource.Failure(e)
            }
        }
    }


}