package com.branchx.agent.ui.viewmodel.auth

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.BuildConfig
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.repository.AuthRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.data.response.LoginResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appPreference: AppPreference,
    private val apiData: ApiData
) : BaseViewModel() {

    var mobileNumber =""
    var password =""
    var deviceid = ""
    var isLoginCheck = appPreference.isLoginCheck

    init {
        if (appPreference.isLoginCheck) {
            mobileNumber = appPreference.mobile
            password = appPreference.password
        }
        if(BuildConfig.DEBUG){
//            mobileNumber = "9174735000"
//            password="effectively@57#67"
//              mobileNumber = "9970581474"
//              password="advance@5#77"

            isLoginCheck = true
        }
    }

    private val _loginObserver = SingleMutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginObserver


    fun onLoginClick(view: View?) {
        if (!validateLoginInputs()) return@onLoginClick
        _loginObserver.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { repository.appLogin(apiData.loginData(mobileNumber, password)) }
                }
                if(response.status ==1) setLoginCheck(response)
                _loginObserver.value = Resource.Success(response)
            } catch (e: Exception) {
                _loginObserver.value = Resource.Failure(e)
            }
        }
    }


    private fun validateLoginInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (mobileNumber.length > 8) {
            if (password.length > 5) {
                return true
            } else "Password should be 6 digits"
        } else "Enter valid login id"
        errMessageObs.value = message
        return false
    }

    private fun setLoginCheck(response: LoginResponse) {

        if (response.status == 1) {

            appPreference.password = password
            appPreference.mobile = mobileNumber

            //save user to Preference
            appPreference.user = response.user
            appPreference.isLoginCheck = isLoginCheck
        }
    }


    private val _forgotPasswordObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val forgotPasswordObs: LiveData<Resource<CommonResponse>> = _forgotPasswordObs


    fun onForgotPasswordClick(view: View?) {
        if (!validateForgotPasswordInputs()) return@onForgotPasswordClick
        _forgotPasswordObs.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { repository.forgotPassword(apiData.forgotPasswordData(mobileNumber, deviceid)) }
                }
                _forgotPasswordObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _forgotPasswordObs.value = Resource.Failure(e)
            }
        }
    }

    private fun validateForgotPasswordInputs(): Boolean {
        errMessageObs.value = AppConstants.EMPTY
        val message = if (mobileNumber.length == 10) {
           return true
        } else "Enter valid 10 digits mobile number"
        errMessageObs.value = message
        return false
    }
}