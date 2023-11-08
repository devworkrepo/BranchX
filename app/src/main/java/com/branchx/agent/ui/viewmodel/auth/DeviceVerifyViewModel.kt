package com.branchx.agent.ui.viewmodel.auth

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.AuthRepository
import com.branchx.agent.data.response.AppGenerateOtp
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class DeviceVerifyViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val apiData : ApiData
) : BaseViewModel() {

    var mobileNumber = AppConstants.EMPTY
    var password = AppConstants.EMPTY
    var otp = AppConstants.EMPTY
    var isLoginOtpRequest : Boolean = false




    private val _verifyDeviceOtp = MutableLiveData<Resource<CommonResponse>>()
    val verifyDeviceOtp: LiveData<Resource<CommonResponse>>
        get() = _verifyDeviceOtp


    fun onVerifyClick(view: View) {
        if(!validateInput())return

        _verifyDeviceOtp.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        if(isLoginOtpRequest)
                             repository.verifyLoginOtp(apiData.loginData(mobileNumber,password, otp =otp))
                        else {
                            repository.verifyDeviceOtp(apiData.loginData(mobileNumber,password,otp =otp)) }
                    }
                }
                _verifyDeviceOtp.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifyDeviceOtp.value = Resource.Failure(e)
            }
        }

    }


    private val _generateOtp = MutableLiveData<Resource<AppGenerateOtp>>()
    val generateOtp: LiveData<Resource<AppGenerateOtp>>
        get() = _generateOtp

    fun verificationOtpRequest() {
        _generateOtp.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.generateOtp(apiData.loginData(mobileNumber,password))
                    }
                }
                _generateOtp.value = Resource.Success(response)
            } catch (e: Exception) {
                _generateOtp.value = Resource.Failure(e)
            }
        }

    }


    private fun validateInput(): Boolean {
        return if (otp.length == 4)
            true
        else {
            errMessageObs.value = "Enter valid 4 character OTP"
            false
        }
    }

}