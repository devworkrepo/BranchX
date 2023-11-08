package com.branchx.agent.ui.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.AepsBankUserResponse
import com.branchx.agent.data.model.AepsTransactionResponse
import com.branchx.agent.data.repository.AepsRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.databinding.ActivityAepsBinding
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.fragment.main.AepsServiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AepsViewModel @Inject constructor(
    private val aepsRepository: AepsRepository,
    private val apiData: ApiData,
    private val appPreference: AppPreference
) : BaseViewModel() {

    var deviceName: String = ""
    var customerMobileNumber: String = ""
    var aadhaarNumber: String = ""
    var amount: String = ""
    var bankName: String = ""
    var bankCode: String = ""
    var pidData = ""
    var deviceSerialNumber = ""
    var transactionType = TransactionType.CASH_WITHDRAWAL


    lateinit var aepsServiceType: AepsServiceType

    var loacation : Location? = null


    private val _aepsBankUserObs = MutableLiveData<Resource<AepsBankUserResponse>>()
    val aepsBankUserObs: LiveData<Resource<AepsBankUserResponse>> = _aepsBankUserObs

    fun fetchBankList() {
        _aepsBankUserObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val bankListResponse = withContext(Dispatchers.IO) {
                    apiRequest {
                        aepsRepository.bankList(
                            apiData.data(hashMapOf())
                        )
                    }
                }

                val userInfoResponse = withContext(Dispatchers.IO) {
                    apiRequest {
                        aepsRepository.aepsUserData(
                            apiData.data(hashMapOf("UserID" to appPreference.user.userId.toString()))
                        )
                    }
                }
                _aepsBankUserObs.value = Resource.Success(
                    AepsBankUserResponse(
                        bankListResponse, userInfoResponse
                    )
                )
            } catch (e: Exception) {
                _aepsBankUserObs.value = Resource.Failure(e)
            }
        }

    }


    private val _transactionObs = MutableLiveData<Resource<AepsTransactionResponse>>()
    val transactionObs: LiveData<Resource<AepsTransactionResponse>> = _transactionObs


    fun aepsTransaction() {
        _transactionObs.value = Resource.Loading()
        viewModelScope.launch {
            try {

                val param =  hashMapOf(
                    "UserID" to appPreference.user.userId.toString(),
                    "userip" to AppUtil.getIpv4HostAddress(),
                    "AepsDeviceid" to deviceSerialNumber,
                    "txtPidData" to pidData,
                    "BankNameCW" to bankCode,
                    "Amount" to amount,
                    "Mobile" to customerMobileNumber,
                    "ConsumerName" to "Not available",
                    "AadharCardNo" to aadhaarNumber,
                    "TransType" to getTransactionTypeData(),
                    "lat" to loacation?.latitude.toString(),
                    "lan" to loacation?.longitude.toString()
                )


                val response = withContext(Dispatchers.IO) {

                    apiRequest {
                        when(aepsServiceType){
                            AepsServiceType.AEPS ->aepsRepository.aepsTransaction(
                                apiData.data(param)
                            )
                            AepsServiceType.AADHAAR_PAY->aepsRepository.aadhaarPayTransaction(
                                apiData.data(param)
                            )
                        }
                    }
                }
                _transactionObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _transactionObs.value = Resource.Failure(e)
            }
        }
    }


    fun getTransactionTypeData() = when (transactionType) {
        TransactionType.BALANCE_ENQUIRY -> "BE"
        TransactionType.CASH_WITHDRAWAL -> "CW"
        TransactionType.MINI_STATEMENT -> "MS"
    }


    fun getTransactionTypeDataFullForm() = when (transactionType) {
        TransactionType.BALANCE_ENQUIRY -> "Balance Enquiry"
        TransactionType.CASH_WITHDRAWAL -> "Cash Withdrawal"
        TransactionType.MINI_STATEMENT -> "Mini Statement"
    }


    fun validateInput(binding: ActivityAepsBinding): Boolean {
        var isValid = true

        amount = binding.edAmount.text.toString()
        customerMobileNumber = binding.edCustomerMobile.text.toString()
        aadhaarNumber = binding.edAadharNumber.text.toString()


        if (amount.isEmpty()) amount = "0"


        if (bankName.isEmpty()) {
            isValid = false
            binding.tilBankName.apply {
                error = "Select Bank"
                isErrorEnabled = true
            }
        } else binding.tilBankName.isErrorEnabled = false

        if (customerMobileNumber.length != 10) {
            isValid = false
            binding.tilCustomerMobile.apply {
                error = "Enter 10 digit customer mobile number"
                isErrorEnabled = true
            }
        } else binding.tilCustomerMobile.isErrorEnabled = false


        if (aadhaarNumber.length != 14) {
            isValid = false
            binding.tilAadharNumber.apply {
                error = "Enter 12 digit aadhaar number"
                isErrorEnabled = true
            }
        } else binding.tilAadharNumber.isErrorEnabled = false



        if (amount.toDouble() !in 100.0..10000.0 &&
            TransactionType.CASH_WITHDRAWAL == transactionType
        ) {
            isValid = false
            binding.tilAmount.apply {
                error = "Enter min amount 100 Rs."
                isErrorEnabled = true
            }
        } else binding.tilAmount.isErrorEnabled = false

        if (aadhaarNumber.contains("-"))
            aadhaarNumber = aadhaarNumber.replace("-", "")
        return isValid

    }



    private val _f2fAuthObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val f2fAuthObs: LiveData<Resource<CommonResponse>> = _f2fAuthObs


    fun proceedF2FAuth() {
        _f2fAuthObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val param =  hashMapOf(
                    "serialNumber" to deviceSerialNumber,
                    "txtPidData" to pidData,
                    "BankNameCW" to bankCode,
                    "txtlat" to loacation?.latitude.toString(),
                    "txtlan" to loacation?.longitude.toString(),
                    "serviceType" to if(aepsServiceType == AepsServiceType.AEPS) "AEPS" else "AP"
                )

                val response = withContext(Dispatchers.IO) {

                    apiRequest {
                        aepsRepository.f2fAuth(apiData.data(param))
                    }
                }
                _f2fAuthObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _f2fAuthObs.value = Resource.Failure(e)
            }
        }
    }

    enum class TransactionType {
        CASH_WITHDRAWAL, BALANCE_ENQUIRY,MINI_STATEMENT
    }
}