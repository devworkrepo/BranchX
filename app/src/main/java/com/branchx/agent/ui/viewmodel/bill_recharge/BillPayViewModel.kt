package com.branchx.agent.ui.viewmodel.bill_recharge

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.RechargeBillRepository
import com.branchx.agent.data.response.BillPaymentResponse
import com.branchx.agent.data.response.FetchBillResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Exceptions
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BillPayViewModel @Inject constructor(
    private val apiData: ApiData,
    private val rechargeBillRepository: RechargeBillRepository
) : BaseViewModel() {

    var serviceType: ServiceType?= null
    var dueDate:String? = null
     var customerName : String?=null

    //INIT FIELDS
    var operator: Int? = null
    var eRoNoTF: Int = 0

    //XML FIELDS
    var caOrBillNumber = ""
    var mobileNumber = ""
    var amount: String = AppConstants.EMPTY
    var eroNumber: String = AppConstants.EMPTY

    //LIVE DATA
    private val _fetchBillObs = MutableLiveData<Resource<FetchBillResponse>>()
    val fetchBillObs: LiveData<Resource<FetchBillResponse>> = _fetchBillObs

    private val _billPaymentObs = SingleMutableLiveData<Resource<BillPaymentResponse>>()
    val billPaymentObs: LiveData<Resource<BillPaymentResponse>> = _billPaymentObs


    /*
   * desc: fetching bill information for payment
   * call: xml layout (data binding)
   * type: coroutine method
   * api : BillFetch
   * */
    fun fetchBillInfo(view: View?) {

        if (!validateFetchBillInputs()) return@fetchBillInfo
        _fetchBillObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {

                        if (operator == null) throw Exceptions.GeneralException("Operator not found!")
                        rechargeBillRepository.fetchBillInfo(
                            apiData.data(
                                hashMapOf(
                                    "CANumber" to caOrBillNumber,
                                    "Operator" to operator!!.toString(),
                                    "ConsumerNo" to mobileNumber
                                )
                            )
                        )
                    }
                }
                _fetchBillObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _fetchBillObs.value = Resource.Failure(e)
            }
        }
    }


    /*
    * desc: final payment method for bill payment
    * call: xml layout (data binding)
    * type: coroutine method
    * api : BillPayment
    * */
    fun makePayment(view: View?) {

        if (!validateMakePaymentInputs()) return@makePayment
        _billPaymentObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {

                        val param =  hashMapOf(
                            "Operator" to operator!!.toString(),
                            "Account" to caOrBillNumber,
                            "Amount" to amount,
                            "Option1" to eroNumber,
                            "Option2" to AppConstants.EMPTY,
                            "Consumerno" to mobileNumber,
                            "BillerDuedate" to dueDate.orEmpty(),
                            "BillerName" to customerName.orEmpty(),
                        )

                        if (operator == null) throw Exceptions.GeneralException("Operator not found!")
                        else{
                            when(serviceType){
                                ServiceType.ELECTRICITY->rechargeBillRepository.makeElectricityBillPayment(apiData.data(param))
                                else->rechargeBillRepository.makeOtherBillPayment(apiData.data(param))
                            }

                        }
                    }
                }
                _billPaymentObs.value = Resource.Success(response)
            } catch (e: Exception) { _billPaymentObs.value = Resource.Failure(e) }
        }
    }

    //validations methods
    private fun validateFetchBillInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (caOrBillNumber.length >= 6) {
            if (mobileNumber.length == 10) return true
            else "Enter valid 10 digits mobile number"
        } else "ca or bill number should equal or greater that 6 digits"
        errMessageObs.value = message
        return false
    }

    private fun validateMakePaymentInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (caOrBillNumber.length >= 6) {
            if (mobileNumber.length == 10) {
                if (eRoNoTF == 1) {
                    if (eroNumber.isNotEmpty()) {
                        return true
                    } else "Ero number can't be empty!"
                } else return true
            } else "Enter valid 10 digits mobile number"
        } else "ca or bill number should equal or greater than 6 digits"
        errMessageObs.value = message
        return false
    }


}