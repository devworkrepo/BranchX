package com.branchx.agent.ui.viewmodel

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.repository.MatmRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.data.response.MatmPrefDetail
import com.branchx.agent.data.response.MatmRequestResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.LocationService
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.fingpay.microatmsdk.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MatmViewModel @Inject constructor(
    private val matmRepository: MatmRepository,
    private val apiData: ApiData,
    private val appPreference: AppPreference
) : BaseViewModel() {


    var matmResStatus: Boolean = false
    var matmResBankRRN: String = ""
    var matmResMessage: String = ""
    var matmResCardNumber: String = ""
    var matmResBankName: String = ""
    var matmResTransAmount: String = ""
    var matmResBalAmount: String = ""


    lateinit var providerTxnId: String

    var matmTxnType: Int = Constants.CASH_WITHDRAWAL
    var customerMobileNumber: String = ""
    var amount: String = ""
    var txnType: String = ""
    var deviceId: String = ""
    var latitude: String = ""
    var longitude: String = ""

    val txnTypeResourceIdObs = MutableLiveData<Int>()

    private val _requestMatmObs = SingleMutableLiveData<Resource<MatmRequestResponse>>()
    val requestMatmObs: LiveData<Resource<MatmRequestResponse>> = _requestMatmObs


    private val _requestPermissionObs = SingleMutableLiveData<Boolean>()
    val requestPermissionObs: LiveData<Boolean> = _requestPermissionObs

    init {
        txnTypeResourceIdObs.value = R.id.rb_cw
    }


    fun onLaunchButtonClick(view: View?) {
        if (!validateInputForMatmTxn()) return
        _requestPermissionObs.value = true
    }


    private val _historyScreenLaunchObs = SingleMutableLiveData<Bundle>()
    val historyScreenLaunchObs: LiveData<Bundle> = _historyScreenLaunchObs

    fun onHistoryButtonClick(view: View?) {
        errMessageObs.value = ""
        if (appPreference.matmDetail.merchantId.isEmpty()) {
            errMessageObs.value = "Initiate a transaction first, than try it again!"
            return@onHistoryButtonClick
        }

        val matmPrefDetail = appPreference.matmDetail
        val dataBundle = bundleOf(
            Constants.MERCHANT_USERID to matmPrefDetail.merchantId,
            Constants.MERCHANT_PASSWORD to matmPrefDetail.merchantPin,
            Constants.SUPER_MERCHANTID to matmPrefDetail.superMerchantId,
            Constants.IMEI to matmPrefDetail.deviceId
        )
        _historyScreenLaunchObs.value = dataBundle
    }


    fun fetchLocation(mLocationManager: LocationManager) {
        _requestMatmObs.value = Resource.Loading()
        LocationService.getCurrentLocation(mLocationManager)
        LocationService.setupListener(object : LocationService.MLocationListener {
            override fun onLocationChange(location: Location) {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                requestMatmData()
            }
        })
    }

    private fun requestMatmData() {

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        matmRepository.requestMatmData(
                            apiData.data(
                                hashMapOf(
                                    "transtype" to txnType,
                                    "amount" to amount,
                                    "lat" to latitude,
                                    "lan" to longitude,
                                    "mobile" to customerMobileNumber,
                                    "consumername" to "Not available",
                                    "userip" to AppUtil.getIpv4HostAddress()
                                )
                            )
                        )
                    }

                }
                saveDataToMatmPreference(result)
                _requestMatmObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _requestMatmObs.value = Resource.Failure(e)
            }
        }
    }

    private fun saveDataToMatmPreference(result: MatmRequestResponse) {
        appPreference.matmDetail =
            MatmPrefDetail(
                result.loginId.orEmpty(),
                result.loginPin.orEmpty(),
                result.superMerchantId.orEmpty(),
                deviceId
            )
    }


    private val _updateMatmRequestObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val updateMatmRequestObs: LiveData<Resource<CommonResponse>> = _updateMatmRequestObs

    fun updateMatmRequest() {
        _updateMatmRequestObs.value =
            Resource.Success(CommonResponse(status = 1, message = "Success"))
        return

        viewModelScope.launch {
            try {
                _updateMatmRequestObs.value = Resource.Loading()
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        matmRepository.updateMatmData(
                            apiData.data(
                                hashMapOf(
                                    "status" to if (matmResStatus) 1.toString() else 2.toString(),
                                    "bankRRN" to matmResBankRRN,
                                    "bankName" to matmResBankName,
                                    "message" to matmResMessage,
                                    "providerTxnId" to providerTxnId,
                                    "cardNumber" to matmResCardNumber
                                )
                            )
                        )
                    }
                }
                _updateMatmRequestObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _updateMatmRequestObs.value = Resource.Failure(e)
            }
        }
    }


    private fun validateInputForMatmTxn(): Boolean {
        errMessageObs.value = ""
        val message = if (customerMobileNumber.length == 10) {
            if (txnType == "CW") {
                try {
                    if (amount.toDouble() >= 10) return true
                    else "Enter amount 10 and above"
                } catch (e: Exception) {
                    "Invalid amount format"
                }
            } else {
                amount = "0"
                return true
            }
        } else "Enter valid 10 digits customer mobile number"
        errMessageObs.value = message
        return false
    }
}