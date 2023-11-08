package com.branchx.agent.ui.viewmodel.auth

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.AuthRepository
import com.branchx.agent.data.response.CityListResponse
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.toByteArray
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val apiData: ApiData
) : BaseViewModel() {

    var username: String = AppConstants.EMPTY
    var mobileNumber: String = AppConstants.EMPTY
    var alternateMobileNumber: String = AppConstants.EMPTY
    var emailId: String = AppConstants.EMPTY
    var address: String = AppConstants.EMPTY
    var pincode: String = AppConstants.EMPTY
    var aadhaarNumber: String = AppConstants.EMPTY
    var panNumber: String = AppConstants.EMPTY
    var gstNumber: String = AppConstants.EMPTY

    var aadhaarFrontBitmap: Bitmap? = null
    var aadhaarBackBitmap: Bitmap? = null
    var panCardBitmap: Bitmap? = null
    var profileBitmap: Bitmap? = null

    var stateName = AppConstants.EMPTY
    var cityId = AppConstants.EMPTY


    private val _registerUserObserver = SingleMutableLiveData<Resource<CommonResponse>>()
    val registerUserObserver: LiveData<Resource<CommonResponse>>
        get() = _registerUserObserver

    fun registerUser() {
        errMessageSingleObs.value = AppConstants.EMPTY
        if (!validateIInput()) return@registerUser
        if (!validateDialogInput()) return@registerUser
        if (!validateFile()) return@registerUser

        _registerUserObserver.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { repository.registerUser(getUserData()) }
                }
                _registerUserObserver.value = Resource.Success(response)
            } catch (e: Exception) {
                _registerUserObserver.value = Resource.Failure(e)
            }
        }
    }

    private val _userListObserver = SingleMutableLiveData<Resource<CommonResponse>>()
    val userListObserver: LiveData<Resource<CommonResponse>>
        get() = _userListObserver

    fun showuUser() {
        _userListObserver.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { repository.userList(apiData.data()) }
                }
                _userListObserver.value = Resource.Success(response)
            } catch (e: Exception) {
                _userListObserver.value = Resource.Failure(e)
            }
        }
    }

    private fun getUserData(): JsonObject {
        val base64AadhaarFront =
            aadhaarFrontBitmap?.let {
                String(
                    Base64.encode(aadhaarFrontBitmap?.toByteArray(), Base64.DEFAULT),
                    Charsets.UTF_8
                )
            } ?: ""

        val base64AadhaarBack =
            aadhaarBackBitmap?.let {
                String(
                    Base64.encode(aadhaarBackBitmap?.toByteArray(), Base64.DEFAULT),
                    Charsets.UTF_8
                )
            } ?: ""

        val base64ProfilePic =
            profileBitmap?.let {
                String(
                    Base64.encode(profileBitmap?.toByteArray(), Base64.DEFAULT),
                    Charsets.UTF_8
                )
            } ?: ""

        val base64PanImage =
            panCardBitmap?.let {
                String(
                    Base64.encode(panCardBitmap?.toByteArray(), Base64.DEFAULT),
                    Charsets.UTF_8
                )
            } ?: ""
        return apiData.data(
            hashMapOf(
                //unused
                "userid" to "0",
                "usertype" to "5",
                "ipaddress" to "",
                "lastmodifiedby" to "",
                //param name address etc..
                "name" to username,
                "emailid" to emailId,
                "mobileno" to mobileNumber,
                "alternatemobileno" to alternateMobileNumber,
                "address" to address,
                "country" to "113",
                "city" to cityId,
                "state" to stateName,
                "pincode" to pincode,
                "gstno" to gstNumber,
                //file number
                "pancardno" to panNumber,
                "aadharnumber" to aadhaarNumber,
                //file name
                "pcimgname" to "${username}_pancard.jpg",
                "acimgname" to "${username}_aadhar_front.jpg",
                "acbackimgname" to "${username}_aadhar_back.jpg",
                //file data
                "acimg" to base64AadhaarFront,
                "acbackimg" to base64AadhaarBack,
                "profileimg" to base64ProfilePic,
                "pcimg" to base64PanImage,
            )
        )
    }

    private fun validateIInput(): Boolean {
        val message: String = if (username.isNotEmpty()) {
            if (mobileNumber.length == 10) {
                if (alternateMobileNumber.length == 10) {
                    if (emailId.isNotEmpty()) {
                        if (address.isNotEmpty()) {
                            if (pincode.length == 6) {
                                if (aadhaarNumber.length == 12) {
                                    if (panNumber.isNotEmpty()) {
                                        if (gstNumber.isNotEmpty()) {
                                            return true
                                        } else "enter gst number"
                                    } else "enter pan number"
                                } else "enter valid 12 digit aadhar number"
                            } else "enter valid 6 digit pincode"
                        } else "enter address"
                    } else "enter email address"
                } else "enter valid 10 digit alternate mobile number"
            } else "enter valid 10 digit mobile number"
        } else "enter username"
        errMessageSingleObs.value = message
        return false
    }

    private fun validateFile(): Boolean {
        val message: String = if (aadhaarFrontBitmap != null) {
            if (aadhaarBackBitmap != null) {
                if (panCardBitmap != null) {
                    if (profileBitmap != null) {
                        return true
                    } else "select user profile pic"
                } else "select pan card pic"
            } else "select Aadhaar back pic"
        } else "select Aadhaar front pic"
        errMessageSingleObs.value = message
        return false
    }

    private fun validateDialogInput(): Boolean {
        var message = ""
        if (stateName.isNotEmpty()) {
            if (cityId.isNotEmpty()) {
                return true
            } else message = "Select City"
        } else message = "Select State"
        errMessageSingleObs.value = message
        return false
    }

  /*  private val _stateListObserver = SingleMutableLiveData<Resource<StateListResponse>>()
    val stateListObserver: LiveData<Resource<StateListResponse>>
        get() = _stateListObserver

    fun fetchStateList() {
        _stateListObserver.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.fetchStateList("113")
                    }
                }
                _stateListObserver.value = Resource.Success(data)

            } catch (e: Exception) {
                _stateListObserver.value = Resource.Failure(e)
            }
        }
    }*/

    private val _cityListObserver = SingleMutableLiveData<Resource<CityListResponse>>()
    val cityListObserver: LiveData<Resource<CityListResponse>>  = _cityListObserver

    fun fetchCityList() {
        _cityListObserver.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    apiRequest {
                        repository.fetchCityList();
                    }
                }
                _cityListObserver.value = Resource.Success(data)

            } catch (e: Exception) {
                _cityListObserver.value = Resource.Failure(e)
            }
        }
    }


}