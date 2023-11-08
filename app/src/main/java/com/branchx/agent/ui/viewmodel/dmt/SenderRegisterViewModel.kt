package com.branchx.agent.ui.viewmodel.dmt

import android.util.Log
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
class SenderRegisterViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository
) : BaseViewModel() {

    var dmtType : DmtType = DmtType.DMT_TWO


    //layout fields
    var senderMobileNumber = AppConstants.EMPTY
    var senderFirstName = AppConstants.EMPTY
    var senderLastName = AppConstants.EMPTY

    //live data
    private val _registerSenderObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val registerSenderObs: LiveData<Resource<CommonResponse>> = _registerSenderObs


    /*
   * desc: register sender requesting for otp
   * call: xml layout (data binding)
   * type: coroutine method
   * api : Dmt1rAdd
   * */
    fun onSubmitButtonClick(view: View?) {

        if (!validateRegisterSenderInputs()) return@onSubmitButtonClick
        _registerSenderObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        when (dmtType) {
                            DmtType.DMT_ONE ->
                                dmtRepository.dmtOneRegisterSender(
                                    apiData.data(
                                        hashMapOf(
                                            "mobile" to senderMobileNumber,
                                            "fname" to senderFirstName,
                                            "lname" to senderLastName
                                        )
                                    )
                                )
                            DmtType.DMT_TWO -> dmtTwoRepository.registerSender(
                                apiData.data(
                                    hashMapOf(
                                        "mobile" to senderMobileNumber,
                                        "fname" to senderFirstName,
                                        "lname" to senderLastName
                                    )
                                )
                            )

                        }
                    }
                }
                _registerSenderObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _registerSenderObs.value = Resource.Failure(e)
            }
        }
        Log.d("FirstNameAndLasteEndd","$senderFirstName $senderLastName ")

    }


    //INPUT VALIDATION
    private fun validateRegisterSenderInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (senderMobileNumber.length == 10)
            if (senderFirstName.length >= 3) {
                if (senderLastName.length >= 3) return true
                else "Sender last name should be greater than or equal to 3 characters"
            } else "Sender first name should be greater than or equal to 3 characters"
        else "Enter valid 10 digits mobile number"
        errMessageObs.value = message
        return false
    }

}