package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.AepsBank
import com.branchx.agent.data.model.AepsUserInfo
import com.branchx.agent.data.repository.AepsRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EKycViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val apiData: ApiData,
    private val aepsRepository: AepsRepository
) : BaseViewModel() {

    lateinit var useInfo: AepsUserInfo
    lateinit var aepsList: List<AepsBank>

    var stepType = StepType.STEP_ONE
    var otpType = OTPType.SEND


    var pidData = ""
    var aadhaarNumber = ""
    var otp = ""
    var bankName = ""
    var bankId = ""
    var deviceSerialNumber = ""
    var latitude = ""
    var longitude = ""
    var ipAddress = AppUtil.getIpv4HostAddress()


    private val _sendOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val sendObs: LiveData<Resource<CommonResponse>> = _sendOtpObs

    fun sendKycOtp() {
        _sendOtpObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    apiRequest { aepsRepository.verifyDevice(apiData.data(hashMapOf(
                        "lat" to latitude,
                        "lan" to longitude,
                        "userip" to ipAddress,
                        "AepsDeviceid" to deviceSerialNumber,
                    ))) }
                }

                _sendOtpObs.value = Resource.Success(response);
            } catch (e: Exception) {
                _sendOtpObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onBoardingObs = MutableLiveData<Resource<CommonResponse>>()
    val onBoardingObs: LiveData<Resource<CommonResponse>> = _onBoardingObs

    fun onboard() {
        _onBoardingObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    apiRequest { aepsRepository.aepsOnboard(apiData.data(hashMapOf(
                        "lat" to latitude,
                        "lan" to longitude,
                        "userip" to ipAddress,
                    ))) }
                }

                _onBoardingObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _onBoardingObs.value = Resource.Failure(e)
            }
        }

    }


    private val _verifyOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val verifyOtpObs: LiveData<Resource<CommonResponse>> = _verifyOtpObs
    fun verifyOtp() {
        _verifyOtpObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response  = withContext(Dispatchers.IO){
                    apiRequest { aepsRepository.validateVerifyDevice(apiData.data(hashMapOf(
                        "userip" to ipAddress,
                        "AepsDeviceid" to deviceSerialNumber,
                        "otp" to otp
                    ))) }
                }
                _verifyOtpObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifyOtpObs.value = Resource.Failure(e)
            }
        }
    }


    private val _authObs = MutableLiveData<Resource<CommonResponse>>()
    val authObs: LiveData<Resource<CommonResponse>> = _authObs
    fun proceedEkyc() {
        _authObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
               val response =  withContext(Dispatchers.IO){
                    apiRequest {
                        aepsRepository.authKyc(apiData.data(hashMapOf(
                            "userip" to ipAddress,
                            "AepsDeviceid" to deviceSerialNumber,
                            "BankNameCW" to bankId,
                            "txtPidData" to pidData
                        )))
                    }
                }
                _authObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _authObs.value = Resource.Failure(e)
            }
        }
    }

    enum class OTPType {
        SEND, RESEND
    }

    enum class StepType {
        STEP_ONE, STEP_TWO
    }
}

