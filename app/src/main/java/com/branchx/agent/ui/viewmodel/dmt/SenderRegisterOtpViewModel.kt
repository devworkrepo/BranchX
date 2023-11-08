package com.branchx.agent.ui.viewmodel.dmt

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.DmtRepository
import com.branchx.agent.data.repository.DmtTwoRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SenderRegisterOtpViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository
) : BaseViewModel() {

    var dmtType: DmtType = DmtType.DMT_TWO
    //layout fields
    var otp = AppConstants.EMPTY
    var senderMobileNumber = AppConstants.EMPTY
    var firstName = AppConstants.EMPTY
    var lastName = AppConstants.EMPTY


    //live data
    private val _registerSenderOtpObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val registerSenderOtpObs: LiveData<Resource<CommonResponse>> = _registerSenderOtpObs


    /*
   * desc: confirm register sender with otp
   * call: xml layout (data binding)
   * type: coroutine method
   * api : Dmt1rAddotp
   * */
    fun onSubmitButtonClick(view: View?) {
        if (!validateRegisterSenderInputs()) return@onSubmitButtonClick
        _registerSenderOtpObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                         dmtTwoRepository.registerSenderVerify(apiData.data(hashMapOf(
                        "mobile" to senderMobileNumber,
                        "otp" to otp,
                        "fname" to firstName,
                        "lname" to lastName
                        )))
                    }
                }
                _registerSenderOtpObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _registerSenderOtpObs.value = Resource.Failure(e)
            }
        }

    }


    private val _onResendOtpObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val onResendOtpObs: LiveData<Resource<CommonResponse>> = _onResendOtpObs


    fun resendOtp() {
        _onResendOtpObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        dmtRepository.dmtOneRendOtp(apiData.data(hashMapOf(
                            "mobile" to senderMobileNumber,
                        )))
                    }

                }
                _onResendOtpObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onResendOtpObs.value = Resource.Failure(e)
            }
        }
    }


    //INPUT VALIDATION
    private fun validateRegisterSenderInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (senderMobileNumber.length == 10)
            if (otp.length == 4)return true
            else "Enter valid 4 digits Otp!"
        else "Enter valid 10 digits mobile number!"
        errMessageObs.value = message
        return false
    }

}