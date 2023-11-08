package com.branchx.agent.ui.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.AuthRepository
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.util.SingleMutableLiveData
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val apiData: ApiData,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    var currentPassword: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""


    private val _onPasswordChangeObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val onPasswordChangeObs: LiveData<Resource<CommonResponse>> =
        _onPasswordChangeObs

    fun changePassword() {
        _onPasswordChangeObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        authRepository.changePassword(
                            apiData.data(
                                hashMapOf(
                                    "newpassword" to newPassword,
                                    "otporoldpass" to currentPassword,
                                    "option" to "oldpassword"
                                )
                            )
                        )
                    }
                }
                _onPasswordChangeObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onPasswordChangeObs.value = Resource.Failure(e)
            }
        }
    }
}