package com.branchx.agent.ui.viewmodel.bill_recharge

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.RechargeBillRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val apiData: ApiData,
    private val rechargeBillpayRepository: RechargeBillRepository
) : BaseViewModel() {

    var operator: Int = 0
    var number = AppConstants.EMPTY
    var amount: String = AppConstants.EMPTY
    var serviceType  : ServiceType? = null


    private val _rechargeObs = MutableLiveData<Resource<CommonResponse>>()
    val rechargeObs: LiveData<Resource<CommonResponse>> = _rechargeObs


    fun makeRecharge(view: View?) {

        if (!validateInputs()) return@makeRecharge
        _rechargeObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        rechargeBillpayRepository.makeRecharge(
                            apiData.data(
                                hashMapOf(
                                    "Operator" to operator.toString(),
                                    "Mobile" to number,
                                    "Amount" to amount
                                )
                            )
                        )
                    }
                }
                _rechargeObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _rechargeObs.value = Resource.Failure(e)
            }
        }
    }


    private fun validateInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        if(serviceType == ServiceType.DTH){
            if(number.length !in 6..14){
                errMessageObs.value = "Enter 6 - 14 digits DTH number"
                return false
            }
        }
        else {
            if(number.length != 10){
                errMessageObs.value = "Enter 10 digits mobile number"
                return false
            }
        }

        if(amount.isEmpty()){
            errMessageObs.value = "Empty can't be empty"
            return false
        }

        if(amount.toDouble() < 10.0){
            errMessageObs.value = "Enter amount 10 and greater"
            return false
        }

        if(operator == 0){
            errMessageObs.value = "Operator not found"
            return false
        }
        return true
    }
}