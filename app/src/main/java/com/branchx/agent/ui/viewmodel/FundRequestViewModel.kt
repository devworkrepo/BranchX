package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.FundRequestBankResponse
import com.branchx.agent.data.model.FundRequestInitialResponse
import com.branchx.agent.data.repository.FundRequestRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FundRequestViewModel @Inject constructor(
    private val fundRequestRepository: FundRequestRepository,
    private val apiData: ApiData,
    private val appPreference: AppPreference
) : BaseViewModel() {

     var userId : String = ""


    var file: File? = null
    var paymentDate: String = ""
    var paymentType: String = ""
    var paymentTo: String = ""
    var bankName: String = ""
    var amount: String = ""
    var extraFieldOne: String = ""
    var extraFieldTwo: String = ""
    var fieldOneTitle: String = "Field One"
    var fieldTwoTitle: String = "Field Two"


    private val _fundRequestInitialDataObs = MutableLiveData<Resource<FundRequestInitialResponse>>()
    val fundRequestInitialDataObs: LiveData<Resource<FundRequestInitialResponse>> =
        _fundRequestInitialDataObs

    fun fetchFundRequestInitialData() {

        _fundRequestInitialDataObs.value = Resource.Loading()
        viewModelScope.launch {
            try {

                coroutineScope {
                    val paymentModeDeferred = withContext(Dispatchers.IO) {
                        apiRequest { fundRequestRepository.fetchPaymentMode(apiData.data()) }
                    }

                    val bankListDeferred = withContext(Dispatchers.IO) {
                        apiRequest {
                            fundRequestRepository.fetchBankList(apiData.data(
                                hashMapOf("Userid" to "2000")))
                        }
                    }
                    val userTypeDeferred = withContext(Dispatchers.IO) {
                        apiRequest { fundRequestRepository.fetchRequestUserType(apiData.data()) }
                    }


                    val data = FundRequestInitialResponse(
                        bankListDeferred,
                        paymentModeDeferred,
                        userTypeDeferred
                    )
                    _fundRequestInitialDataObs.postValue(Resource.Success(data))
                }
            } catch (e: Exception) {
                _fundRequestInitialDataObs.value = Resource.Failure(e)
            }
        }
    }


    private val _fundRequestBankResponse = MutableLiveData<Resource<FundRequestBankResponse>>()
    val fundRequestBankResponseObs: LiveData<Resource<FundRequestBankResponse>> =
        _fundRequestBankResponse

    fun fetchFundRequestBankResponseData(userIdForList: String) {
        _fundRequestBankResponse.value = Resource.Loading()
        _fundRequestBankResponse.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val bankListDeferred = withContext(Dispatchers.IO) {
                    apiRequest {
                        fundRequestRepository.fetchBankList(apiData.data(
                            hashMapOf("Userid" to userIdForList)))
                    }
                }
                _fundRequestBankResponse.value = Resource.Success(bankListDeferred)

            } catch (e: Exception) {
                _fundRequestBankResponse.value = Resource.Failure(e)
            }
        }    }

    private val _fundRequestObs = MutableLiveData<Resource<CommonResponse>>()
    val fundRequestObs: LiveData<Resource<CommonResponse>> = _fundRequestObs

    fun makeRequest() {

        _fundRequestObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val paymentModeDeferred = withContext(Dispatchers.IO) {
                    apiRequest {
                        fundRequestRepository.makeRequest(
                            apiData.data(
                                hashMapOf(
                                    "amount" to amount,
                                    "BankName" to bankName,
                                    "depositdate" to paymentDate,
                                    "paymentmode" to paymentType,
                                    "UserType" to paymentTo,
                                    "slipimg" to "",
                                    "remarks" to "$fieldOneTitle - $extraFieldOne, $fieldTwoTitle - $extraFieldTwo",
                                )
                            )
                        )
                    }
                }
                _fundRequestObs.value = Resource.Success(paymentModeDeferred)

            } catch (e: Exception) {
                _fundRequestObs.value = Resource.Failure(e)
            }
        }
    }

    fun validateInput(): Boolean {
        errMessageObs.value = ""

        if (paymentTo.isEmpty()) {
            errMessageObs.value = "Payment To : is not available please again after sometime"
            return false
        }
        if (paymentType.isEmpty()) {
            errMessageObs.value = "Payment Mode : is not available please again after sometime"
            return false
        }

        if (bankName.isEmpty()) {
            errMessageObs.value = "Bank : is not available please select bank first"
            return false
        }

        if (extraFieldOne.isEmpty()) {
            errMessageObs.value = "$fieldOneTitle: is required!"
            return false
        }

        if (extraFieldTwo.isEmpty()) {
            errMessageObs.value = "$fieldTwoTitle: is required!"
            return false
        }

        if (paymentDate.isEmpty()) {
            errMessageObs.value = "Payment Date : is not available please select date first"
            return false
        }

        if (extraFieldOne.isEmpty()) {
            errMessageObs.value = "Remark field can't be empty!"
            return false
        }

        if (amount.isEmpty()) {
            errMessageObs.value = "Amount can't be blank and zero"
            return false
        }

        if (amount.toDouble() == 0.0) {
            errMessageObs.value = "Amount can't be blank and zero"
            return false
        }
        return true
    }
}