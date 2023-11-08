package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.repository.HomeRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class R2RTransferViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val homeRepository: HomeRepository,
    private val apiData: ApiData
) : BaseViewModel() {


    var mobileNumber = ""
    var amount = ""
    var otp = ""
    var transactionId = ""
    var actionType = ActionType.REQUEST_OTP


    private val _requestOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val requestOtpObs: LiveData<Resource<CommonResponse>> = _requestOtpObs

    fun requestOtp() {

        _requestOtpObs.value = Resource.Loading()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.walletToWalletRequestOtp(
                            apiData.data(
                                hashMapOf(
                                    "withamt" to amount,
                                    "withmobileno" to mobileNumber,
                                )
                            )
                        )
                    }
                }

                _requestOtpObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _requestOtpObs.value = Resource.Failure(e)
            }

        }
    }



    private val _proceedTransactionObs = MutableLiveData<Resource<CommonResponse>>()
    val proceedTransactionObs: LiveData<Resource<CommonResponse>> = _proceedTransactionObs

    fun proceedTransaction() {

        _proceedTransactionObs.value = Resource.Loading()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.walletToWalletRequestVerify(
                            apiData.data(
                                hashMapOf(
                                    "otp" to otp,
                                    "transid" to transactionId,
                                )
                            )
                        )
                    }
                }

                _proceedTransactionObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _proceedTransactionObs.value = Resource.Failure(e)
            }

        }
    }

    enum class ActionType{
        REQUEST_OTP, PROCEED
    }
}