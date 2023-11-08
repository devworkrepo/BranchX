package com.branchx.agent.ui.viewmodel.dmt

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.data.repository.DmtRepository
import com.branchx.agent.data.repository.DmtTwoRepository
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
class SenderSearchViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository,
) : BaseViewModel() {

   var dmtType: DmtType = DmtType.DMT_TWO

    //layout fields
    var senderMobileNumber = AppConstants.EMPTY
    var senderFirstName = AppConstants.EMPTY
    var senderLastName = AppConstants.EMPTY
    var monthlyLimit = AppConstants.EMPTY

    //live data
    private val _senderVerificationObs = SingleMutableLiveData<Resource<SenderInfo>>()
    val senderVerificationObs: LiveData<Resource<SenderInfo>> = _senderVerificationObs


    /*
   * desc: verifying and fetching information for sender
   * call: xml layout (data binding)
   * type: coroutine method
   * api : Dmt1vr
   * */
    fun onSearchButtonClick(view: View?=null) {
        if (!validateSearchInput()) return@onSearchButtonClick
        _senderVerificationObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        when (dmtType) {
                            DmtType.DMT_ONE ->   dmtRepository.dmtOneSenderVerification(apiData.data(hashMapOf("mobile" to senderMobileNumber)))
                            DmtType.DMT_TWO -> dmtTwoRepository.senderVerification(
                                apiData.data(
                                    hashMapOf("mobile" to senderMobileNumber)
                                )
                            )
                        }
                    }
                }
                _senderVerificationObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _senderVerificationObs.value = Resource.Failure(e)
            }
        }

    }


    //INPUT VALIDATION
    private fun validateSearchInput(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        if (senderMobileNumber.length == 10)
            return true
        else errMessageObs.value = "Enter valid 10 digits mobile number"
        return false
    }

}