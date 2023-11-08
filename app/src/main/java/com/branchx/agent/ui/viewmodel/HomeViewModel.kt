package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.Balance
import com.branchx.agent.data.model.notification.NotificationResponse
import com.branchx.agent.data.repository.HomeRepository
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val homeRepository: HomeRepository,
    private val  apiData: ApiData
) : BaseViewModel() {

    private val _notificationObs = MutableLiveData<Resource<NotificationResponse>>()
    val notificationObs: LiveData<Resource<NotificationResponse>> = _notificationObs

    init {
        fetchNotification()
    }




    private fun fetchNotification() {

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { homeRepository.notification(apiData.data()) }
                }
                _notificationObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _notificationObs.value = Resource.Failure(e)
            }

        }

    }


    private val _balanceObs = MutableLiveData<Resource<Balance>>()
    val balanceObs: LiveData<Resource<Balance>> = _balanceObs

    fun fetchBalance(loading: Boolean = false) {

        if (loading)
            _balanceObs.value = Resource.Loading()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest { homeRepository.fetchBalance(appPreference.mobile) }
                }

                _balanceObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _balanceObs.value = Resource.Failure(e)
            }

        }
    }

}