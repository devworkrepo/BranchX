package com.branchx.agent.ui.viewmodel.bill_recharge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.branchx.agent.data.repository.RechargeBillRepository
import com.branchx.agent.data.response.CircleListResponse
import com.branchx.agent.data.response.ProviderResponse
import com.branchx.agent.helper.api.ApiData
import com.branchx.agent.helper.api.Exceptions
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val apiData: ApiData,
    private val rechargeBillRepository: RechargeBillRepository

) : BaseViewModel() {


    var circleProviderType = ElectricityCircleProviderType.CIRCLE
    private val _circleListObs = MutableLiveData<Resource<CircleListResponse>>()
    var circleListObs: LiveData<Resource<CircleListResponse>> = _circleListObs

    fun fetchCircleList() {

        _circleListObs.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                       rechargeBillRepository.getCircleList(apiData.data())
                    }
                }
                _circleListObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _circleListObs.value = Resource.Failure(e)
            }
        }
    }




    private val _circleProviderListObs = MutableLiveData<Resource<ProviderResponse>>()
    var circleProviderListObs: LiveData<Resource<ProviderResponse>> = _circleProviderListObs

    fun fetchCircleProviderList(id: String) {

        _circleProviderListObs.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        rechargeBillRepository.getCircleProvider(id)
                    }
                }
                _circleProviderListObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _circleProviderListObs.value = Resource.Failure(e)
            }
        }
    }




    private val _rechargeProviderObs = MutableLiveData<Resource<ProviderResponse>>()
    var providerObs: LiveData<Resource<ProviderResponse>> = _rechargeProviderObs

    fun getPrepaidProviderList(serviceType: ServiceType?) {


        _rechargeProviderObs.value = Resource.Loading<Nothing>()
        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        serviceType?.let {
                            val data = apiData.data()
                            when (it) {
                                ServiceType.PREPAID -> rechargeBillRepository.getPrepaidProviderList(data)
                                ServiceType.POSTPAID -> rechargeBillRepository.getPostpaidProviderList(data)
                                ServiceType.DTH -> rechargeBillRepository.getDTHProviderList(data)
                                ServiceType.BROADBAND -> rechargeBillRepository.getBrodProviderList(data)
                                ServiceType.GAS -> rechargeBillRepository.getGasProviderList(data)
                                ServiceType.WATER -> rechargeBillRepository.getWaterProviderList(data)
                                ServiceType.LANDLINE -> rechargeBillRepository.getLandProviderList(data)
                                ServiceType.ELECTRICITY -> rechargeBillRepository.getElectricityProviderList(data)
                                else -> throw Exceptions.GeneralException("Invalid service type!")
                            }
                        } ?: run {
                            throw Exceptions.GeneralException("Service not found!")
                        }
                    }
                }
                _rechargeProviderObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _rechargeProviderObs.value = Resource.Failure(e)
            }
        }
    }


    enum class ElectricityCircleProviderType{
        CIRCLE, PROVIDER
    }

}