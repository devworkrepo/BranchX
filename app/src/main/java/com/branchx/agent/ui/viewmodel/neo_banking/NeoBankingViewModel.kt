package com.branchx.agent.ui.viewmodel.neo_banking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.repository.NeoBankingRepository
import com.branchx.agent.data.response.NeoBankingResponse
import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse
import com.branchx.agent.data.response.TPinModel
import com.branchx.agent.data.response.TPinResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class NeoBankingViewModel @Inject constructor(
    private val repository: NeoBankingRepository,
    private val apiData: ApiData,
    private val appPreference: AppPreference,
) : BaseViewModel() {

    var loginId = AppConstants.EMPTY
    var password = AppConstants.EMPTY

    init {
        if (appPreference.isLoginCheck) {
            loginId = appPreference.mobile
            password = appPreference.password
        }

    }


    private var _getNeoOtp = SingleMutableLiveData<Resource<NeoBankingResponse>>()
    val getNeoOtp: SingleMutableLiveData<Resource<NeoBankingResponse>> = _getNeoOtp

    private var _getTopUp = SingleMutableLiveData<Resource<NeoBankingResponse>>()
    val getTopUp: SingleMutableLiveData<Resource<NeoBankingResponse>> = _getTopUp

    private var _verifyNeoOtp = SingleMutableLiveData<Resource<NeoBankingVerifyOtpResponse>>()
    val verifyNeoOtp: SingleMutableLiveData<Resource<NeoBankingVerifyOtpResponse>> = _verifyNeoOtp

    private var _getViewBalanceOtp = SingleMutableLiveData<Resource<NeoBankingResponse>>()
    val getViewBalanceOtp: SingleMutableLiveData<Resource<NeoBankingResponse>> = _getViewBalanceOtp


    private var _getVerifyViewBalanceOtp =
        SingleMutableLiveData<Resource<NeoBankingVerifyOtpResponse>>()
    val getVerifyViewBalanceOtp: SingleMutableLiveData<Resource<NeoBankingVerifyOtpResponse>> =
        _getVerifyViewBalanceOtp


    private var _sharedVerifyOtpResponse = SingleMutableLiveData<NeoBankingVerifyOtpResponse>()
    val sharedVerifyOtpResponse = _sharedVerifyOtpResponse

    private var _sharedMobileNumber = MutableLiveData<String>()
    val sharedMobileNumber = _sharedMobileNumber


    private var _getTPinOtp = SingleMutableLiveData<Resource<TPinResponse>>()
    val getTPinOtp: SingleMutableLiveData<Resource<TPinResponse>> = _getTPinOtp


    private var _generateTPin = SingleMutableLiveData<Resource<TPinResponse>>()
    val generateTPin: SingleMutableLiveData<Resource<TPinResponse>> = _generateTPin


    fun shareOtpVerifyResponse(response: NeoBankingVerifyOtpResponse) {
        _sharedVerifyOtpResponse.postValue(response)
    }

    fun shareMobileNumber(mobileNumber: String) {
        _sharedMobileNumber.postValue(mobileNumber)
    }


    fun getNeoOtpRequest(mobileNumber: String) {
        _getNeoOtp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.getNeoOTP(apiData.neoData(loginId, password, mobileNumber))
                    }
                }
                _getNeoOtp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _getNeoOtp.postValue(Resource.Failure(e))
            }
        }

    }

    fun verifyNeoOtp(mobileNumber: String, otp: String) {
        _verifyNeoOtp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.verifyNeoOtp(
                            apiData.verifyNeoOtp(loginId, password, mobileNumber, otp)
                        )
                    }
                }
                _verifyNeoOtp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _verifyNeoOtp.postValue(Resource.Failure(e))
            }
        }

    }

    fun topUpWallet(tPin: String, mobileNumber: String, amount: String, customerId: String) {
        _getTopUp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.topUpWallet(
                            apiData.topUpWallet(
                                loginId,
                                password,
                                tPin,
                                mobileNumber,
                                customerId,
                                amount
                            )
                        )
                    }
                }
                _getTopUp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _getTopUp.postValue(Resource.Failure(e))
            }
        }

    }


    fun getViewBalanceOtp(mobileNumber: String) {
        _getViewBalanceOtp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.getViewBalanceOtp(
                            apiData.getViewBalanceOtp(
                                loginId,
                                password,
                                mobileNumber
                            )
                        )
                    }
                }
                _getViewBalanceOtp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _getViewBalanceOtp.postValue(Resource.Failure(e))
            }
        }

    }

    fun verifyViewBalanceOtp(mobileNumber: String, otp: String) {
        _getVerifyViewBalanceOtp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.verifyViewBalanceOtp(
                            apiData.verifyViewBalanceOtp(
                                loginId,
                                password,
                                mobileNumber,
                                otp
                            )
                        )
                    }
                }
                _getVerifyViewBalanceOtp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _getVerifyViewBalanceOtp.postValue(Resource.Failure(e))
            }
        }

    }


    fun getTPinOtp() {
        _getTPinOtp.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.generateTPinOTP(apiData.getTPinOtp(loginId, password))
                    }
                }
                _getTPinOtp.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _getTPinOtp.postValue(Resource.Failure(e))
            }
        }
    }

    fun changeTPin(tPinModel:TPinModel) {
        _generateTPin.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.generateTPin(apiData.generateTPin(loginId, password,tPinModel))
                    }
                }
                _generateTPin.postValue(Resource.Success(response))
            } catch (e: Exception) {
                _generateTPin.postValue(Resource.Failure(e))
            }
        }
    }


}