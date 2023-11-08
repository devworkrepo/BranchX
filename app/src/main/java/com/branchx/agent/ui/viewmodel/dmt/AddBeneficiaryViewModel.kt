package com.branchx.agent.ui.viewmodel.dmt

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.DmtRepository
import com.branchx.agent.data.repository.DmtTwoRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.data.response.DmtBankListResponse
import com.branchx.agent.data.response.IfscResponse
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
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddBeneficiaryViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository
) : BaseViewModel() {


    var bankName: String? = null

    //  var bankId: String? = null
    lateinit var senderName: String
    lateinit var senderMobileNumber: String
    //lateinit var dmtType: DmtType
    var dmtType: DmtType = DmtType.DMT_TWO
    var ifscCode: String = AppConstants.EMPTY
    var accountNumber: String = AppConstants.EMPTY
    var beneficiaryName: String = AppConstants.EMPTY


    private val _bankListObs = SingleMutableLiveData<Resource<DmtBankListResponse>>()
    val bankListObs: LiveData<Resource<DmtBankListResponse>> = _bankListObs

    private val _addBeneficiaryObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val addBeneficiaryObs: LiveData<Resource<CommonResponse>> = _addBeneficiaryObs

    init {
        dmtBankList()
    }


    private fun dmtBankList() {

        _bankListObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest { dmtRepository.fetchBankList(apiData.data()) }
                }
                _bankListObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _bankListObs.value = Resource.Failure(e)
            }
        }

    }

    private val _onFetchIfscObs = SingleMutableLiveData<Resource<IfscResponse>>()
    val onFetchIfscObs: LiveData<Resource<IfscResponse>> = _onFetchIfscObs

    fun fetchIfscCode() {
        _onFetchIfscObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest { dmtRepository.fetchIfsc(bankName!!) }
                }
                _onFetchIfscObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onFetchIfscObs.value = Resource.Failure(e)
            }
        }

    }


    fun onBeneficiaryAddButtonClick(view: View?) {
        if (!validateInputForAddBeneficiary())
            return@onBeneficiaryAddButtonClick

        _addBeneficiaryObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        when(dmtType) {
                            DmtType.DMT_ONE -> dmtRepository.dmtOneAddBeneficiary(
                                apiData.data(
                                    hashMapOf(
                                        "mobile" to senderMobileNumber,
                                        "accountNo" to accountNumber,
                                        "bname" to beneficiaryName,
                                        "bankname" to bankName!!,
                                        "ifsccode" to ifscCode
                                    )
                                )
                            )

                            DmtType.DMT_TWO -> dmtTwoRepository.addBeneficiary(
                                apiData.data(
                                    hashMapOf(
                                        "mobile" to senderMobileNumber,
                                        "accountNo" to accountNumber,
                                        "bname" to beneficiaryName,
                                        "bankname" to bankName!!,
                                        "ifsccode" to ifscCode
                                    )
                                )
                            )
                        }
                    }
                }
                _addBeneficiaryObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _addBeneficiaryObs.value = Resource.Failure(e)
            }
        }

    }


    fun onBeneficiaryVerifyAddButtonClick(view: View?) {
        if (!validateInputForAddBeneficiary())
            return@onBeneficiaryVerifyAddButtonClick

        _addBeneficiaryObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {

                        when(dmtType){
                        DmtType.DMT_ONE ->dmtRepository.dmtOneVerifyAddBeneficiary(
                            apiData.data(
                                hashMapOf(
                                    "mobile" to senderMobileNumber,
                                    "accountNo" to accountNumber,
                                    "bname" to beneficiaryName,
                                    "bankname" to bankName!!,
                                    "ifsccode" to ifscCode
                                )
                            )
                        )
                            DmtType.DMT_TWO -> dmtTwoRepository.verifyAddBeneficiary(apiData.data(hashMapOf(
                                "mobile" to senderMobileNumber,
                                "accountNo" to accountNumber,
                                "bname" to beneficiaryName,
                                "bankname" to bankName!!,
                                "ifsccode" to ifscCode
                            )))
                        }
                    }
                }
                _addBeneficiaryObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _addBeneficiaryObs.value = Resource.Failure(e)
            }
        }

    }

    private fun validateInputForAddBeneficiary(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (ifscCode.length == 11) {
            if (accountNumber.length > 8) {

                if (beneficiaryName.length >= 3) {
                    if (validateBankIdName()) return true
                    else "Unable to fetch bank name please try again!"
                } else "Beneficiary name should be equal or greater than 3 characters"

            } else "Account number should be equal or greater than 8 digits"
        } else "Enter valid 11 digits IFSC Code"
        errMessageObs.value = message
        return false
    }

    private fun validateBankIdName() =
        /* bankId != null && */bankName != null


}