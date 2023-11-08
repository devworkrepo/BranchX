package com.branchx.agent.ui.viewmodel.dmt

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.data.repository.DmtRepository
import com.branchx.agent.data.repository.DmtTwoRepository
import com.branchx.agent.data.response.DmtTransactionResponse
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
class MoneyTransactionViewModel @Inject constructor(
    private val apiData: ApiData,
    private val dmtRepository: DmtRepository,
    private val dmtTwoRepository: DmtTwoRepository
) : BaseViewModel() {

    var mpin: String = AppConstants.EMPTY
    var amount: String = AppConstants.EMPTY
    var confirmAmount: String = AppConstants.EMPTY

    var beneficiaryId: String = ""
    var remitterMobile: String = ""
    var sType: String = "IMPS"
    //lateinit var dmtType : DmtType
    var dmtType : DmtType = DmtType.DMT_TWO
    var beneficiaryInfo: BeneficiaryInfo? = null
    var senderInfo : SenderInfo? = null


    private val _transactionObs = SingleMutableLiveData<Resource<DmtTransactionResponse>>()
    val transactionObs: LiveData<Resource<DmtTransactionResponse>> = _transactionObs
    fun onTransferButtonClick(view: View?) {

        if (!validateInputs()) return@onTransferButtonClick

        _transactionObs.value = Resource.Loading()
        viewModelScope.launch {

            try {

                val result = withContext(Dispatchers.IO) {
                    apiRequest {

                        when(dmtType) {
                            DmtType.DMT_ONE -> dmtRepository.dmtOneTransfer(
                                apiData.data(
                                    hashMapOf(
                                        "amt" to confirmAmount,
                                        "rmobileno" to remitterMobile,
                                        "beneid" to beneficiaryId,
                                        "stype" to sType,
                                        "tpin" to mpin,
                                    )
                                )
                            )
                            DmtType.DMT_TWO ->dmtTwoRepository.transfer(
                                apiData.data(
                                    hashMapOf(
                                        "amt" to confirmAmount,
                                        "rmobileno" to senderInfo?.mobileNumber.toString(),
                                        "id" to beneficiaryInfo?.beneficiaryId.toString(),
                                        "beneid" to beneficiaryInfo?.id.orEmpty(),
                                        "stype" to sType,
                                        "tpin" to mpin,
                                    )
                                )
                            )
                        }
                    }
                }
                _transactionObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _transactionObs.value = Resource.Failure(e)
            }
        }

    }


    private fun validateInputs(): Boolean {
        errMessageObs.value = ""
        if (amount != confirmAmount) {
            errMessageObs.value = "Confirm amount doesn't matched!"
            return false
        }
        if (mpin.length != 4) {
            errMessageObs.value = "Enter 4 digits m-pin!"
            return false
        }
        return true
    }


}