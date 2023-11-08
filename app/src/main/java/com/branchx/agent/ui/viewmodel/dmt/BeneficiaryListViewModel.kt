package com.branchx.agent.ui.viewmodel.dmt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.data.model.SenderInfoDmtTwo
import com.branchx.agent.data.repository.DmtRepository
import com.branchx.agent.data.repository.DmtTwoRepository
import com.branchx.agent.data.response.BeneficiaryListResponse
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BeneficiaryListViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository,
) : BaseViewModel() {

    var beneficiaryId = ""
    var beneficiary: BeneficiaryInfo? = null

    // lateinit var dmtType: DmtType
    var dmtType: DmtType = DmtType.DMT_TWO
    lateinit var mobileNumber: String
    var senderName: String = ""
    var monthlyLimit: String = ""
    var isSenderInfoFetched = false
    var senderInfoObs = MutableLiveData<SenderInfo?>(null)
    var senderInfo = MutableLiveData<SenderInfoDmtTwo?>(null)


    private val _beneficiaryListObs = MutableLiveData<Resource<BeneficiaryListResponse>>()
    val beneficiaryListObs: LiveData<Resource<BeneficiaryListResponse>> = _beneficiaryListObs


    fun getBeneficiaryList() {
        _beneficiaryListObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        if (isSenderInfoFetched) {
                            dmtTwoRepository.fetchBeneficiaryList(
                                apiData.data(

                                    hashMapOf(
                                        "mobile" to senderInfoObs.value?.mobileNumber.orEmpty()
                                    )
                                )
                            )

                        } else {
                            fetchSenderData()?.let {


                                dmtTwoRepository.fetchBeneficiaryList(
                                    apiData.data(
                                        hashMapOf("mobile" to senderInfoObs.value?.mobileNumber.orEmpty())
                                        //hashMapOf("mobile" to senderInfoObs.value?.mobileNumber.orEmpty())
                                    )
                                )
                            } ?: run {
                                throw java.lang.Exception("Unable to fetch sender info!")
                            }

                        }

                    }
                }

                _beneficiaryListObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _beneficiaryListObs.value = Resource.Failure(e)
            }
        }
    }

    private val _senderInformation = MutableLiveData<Resource<SenderInfoDmtTwo>>()
    val senderInformation: LiveData<Resource<SenderInfoDmtTwo>> = _senderInformation

    fun setSenderData() {
        _senderInformation.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        dmtTwoRepository.fetchSenderData(
                            apiData.data(
                                hashMapOf("mobile" to senderInfoObs.value?.mobileNumber.orEmpty())
                            )
                        )

                    }
                }
                _senderInformation.value = Resource.Success(result)
            } catch (e: Exception) {
                _senderInformation.value = Resource.Failure(e)
            }


        }
    }


    private suspend fun fetchSenderData(): SenderInfo? {
        val result = withContext(viewModelScope.coroutineContext) {
            dmtTwoRepository.fetchSenderData(
                apiData.data(
                    hashMapOf("mobile" to senderInfoObs.value?.mobileNumber.orEmpty())
                )
            ).body()
        }

        val mInfo = SenderInfo(
            status = result?.status!!,
            message = result.message,
            name = result.name,
            remainingBalance = result.monthlyLimit,
            firstName = "",
            lastName = "",
            mobileNumber = "",
            verify = null
        )
        Log.d("senderInfoObsName", result.name.toString())
        senderName = result.name.toString()
        monthlyLimit = result.monthlyLimit.toString()

        return mInfo

    }


    private val _beneficiaryDeleteObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val beneficiaryDeleteObs: LiveData<Resource<CommonResponse>> = _beneficiaryDeleteObs


    fun deleteDmt2Beneficiary() {
        _beneficiaryDeleteObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        dmtTwoRepository.deleteBeneficiary(
                            apiData.data(
                                hashMapOf(
                                    "benrid" to beneficiary?.id.toString(),
                                    "accountNo" to beneficiary?.Benber.orEmpty(),
                                    "mobile" to senderInfoObs.value?.mobileNumber.orEmpty(),
                                    "bname" to beneficiary?.name.orEmpty(),
                                    "bankname" to beneficiary?.bankName.orEmpty(),
                                    "ifsccode" to beneficiary?.ifsc.orEmpty(),
                                )
                            )
                        )
                    }
                }
                _beneficiaryDeleteObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _beneficiaryDeleteObs.value = Resource.Failure(e)
            }
        }
    }


    private val _beneficiaryVerifyObs = MutableLiveData<Resource<CommonResponse>>()
    val beneficiaryVerifyObs: LiveData<Resource<CommonResponse>> = _beneficiaryVerifyObs


    fun verifyBeneficiary() {
        _beneficiaryVerifyObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {

                        //DMT_TWO
                        dmtTwoRepository.verifyBeneficiary(
                            apiData.data(
                                hashMapOf(
                                    "benrid" to beneficiary?.id.toString(),
                                    "mobile" to senderInfoObs.value?.mobileNumber.orEmpty()

                                )
                            )
                        )
                    }
                }
                _beneficiaryVerifyObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _beneficiaryVerifyObs.value = Resource.Failure(e)
            }
        }
    }


    private val _beneficiaryDeleteOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val beneficiaryDeleteOtpObs: LiveData<Resource<CommonResponse>> = _beneficiaryDeleteOtpObs


}