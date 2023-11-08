package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.model.support.SupportContactResponse
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
class SupportContactViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val apiData: ApiData
) : BaseViewModel() {

    private val _supportNumberObs = MutableLiveData<Resource<SupportContactResponse>>()
    val supportNumberObs: LiveData<Resource<SupportContactResponse>> = _supportNumberObs

    fun fetchSupportNumbers() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { homeRepository.supports(apiData.data()) }
                }
                _supportNumberObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _supportNumberObs.value = Resource.Failure(e)
            }
        }
    }
}