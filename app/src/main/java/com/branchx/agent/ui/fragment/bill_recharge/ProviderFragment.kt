package com.branchx.agent.ui.fragment.bill_recharge

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.data.model.Provider
import com.branchx.agent.data.response.Circle
import com.branchx.agent.databinding.FragmentProviderBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setup
import com.branchx.agent.helper.extns.setupToolbar
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.activity.RechargeBillPayActivity
import com.branchx.agent.ui.adapter.CircleAdapter
import com.branchx.agent.ui.adapter.ProviderAdapter
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.fragment.main.getServiceName
import com.branchx.agent.ui.viewmodel.bill_recharge.ProviderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProviderFragment : BaseFragment<FragmentProviderBinding>(R.layout.fragment_provider) {
    private val viewModel: ProviderViewModel by viewModels()
    private var activityInstance: RechargeBillPayActivity? = null
    private var serviceType: ServiceType? = null

    private lateinit var providerList: List<Provider>
    private var circleList: List<Circle>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (serviceType == ServiceType.ELECTRICITY) {
            activity?.onBackPressedDispatcher?.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (viewModel.circleProviderType == ProviderViewModel.ElectricityCircleProviderType.CIRCLE) {
                            isEnabled = false
                            activity?.onBackPressed()
                        } else {
                            viewModel.circleProviderType =
                                ProviderViewModel.ElectricityCircleProviderType.CIRCLE
                            if (circleList != null)
                                setupCircleList(circleList!!)
                            else viewModel.fetchCircleList()
                        }
                    }
                })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupUIStuff()

        setupToolbar(binding.toolbar.toolbar, getServiceName(serviceType))

        subscribeObservers()

        if (serviceType == ServiceType.ELECTRICITY) {
            if (viewModel.circleProviderType == ProviderViewModel.ElectricityCircleProviderType.PROVIDER) {
                setupProviderList(providerList)
            } else {
                if (circleList == null)
                    viewModel.fetchCircleList()
                else setupCircleList(circleList!!)
                binding.toolbar.toolbar.title = getServiceName(serviceType) + " Circles"
            }
        } else {
            viewModel.getPrepaidProviderList((activity as RechargeBillPayActivity).serviceType)
            binding.toolbar.toolbar.title = getServiceName(serviceType) + " Providers"
        }
    }

    private fun setupUIStuff() {
        activityInstance = (activity as RechargeBillPayActivity)
        serviceType = activityInstance?.serviceType

    }

    private fun subscribeObservers() {
        viewModel.providerObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    contentLayoutVisibility(false)
                }
                is Resource.Success -> {
                    providerList = it.data.providers
                    setupProviderList(it.data.providers)
                    contentLayoutVisibility()
                }
                is Resource.Failure -> {
                    contentLayoutVisibility()
                }
            }
        })

        viewModel.circleListObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    contentLayoutVisibility(false)
                }
                is Resource.Success -> {
                    circleList = it.data.circleList
                    setupCircleList(it.data.circleList)
                    contentLayoutVisibility()
                }
                is Resource.Failure -> {
                    contentLayoutVisibility()
                }
            }
        })

        viewModel.circleProviderListObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    contentLayoutVisibility(false)

                }
                is Resource.Success -> {
                    providerList = it.data.providers
                    setupProviderList(it.data.providers)
                    contentLayoutVisibility()
                }
                is Resource.Failure -> {
                    contentLayoutVisibility()
                }
            }
        })
    }

    private fun setupProviderList(providers: List<Provider>) {
        viewModel.circleProviderType = ProviderViewModel.ElectricityCircleProviderType.PROVIDER
        binding.toolbar.toolbar.title = getServiceName(serviceType) + " Providers"
        val providerAdapter: ProviderAdapter = ProviderAdapter().apply {
            addItems(ArrayList(providers))
            this.context = requireContext()
        }
        binding.recyclerView.setup().adapter = providerAdapter

        providerAdapter.onItemClick = { _, item, _ ->
            serviceType?.let {
                val action: Int? = when (it) {
                    ServiceType.ELECTRICITY, ServiceType.LANDLINE, ServiceType.BROADBAND, ServiceType.WATER, ServiceType.GAS ->
                        R.id.action_providerFragment_to_billPaymentFragment
                    ServiceType.DTH, ServiceType.PREPAID, ServiceType.POSTPAID ->
                        R.id.action_providerFragment_to_rechargeFragment
                    else -> null
                }
                action?.let {
                    findNavController().navigate(action, bundleOf(PROVIDER_KEY to item))
                }
            }
        }

        if(providers.isEmpty()){
            binding.layoutNoDataFound.root.show()
        }else binding.layoutNoDataFound.root.hide()
    }


    private fun setupCircleList(circles: List<Circle>) {
        viewModel.circleProviderType = ProviderViewModel.ElectricityCircleProviderType.CIRCLE
        binding.toolbar.toolbar.title = getServiceName(serviceType) + " Circles"
        val circleAdapter = CircleAdapter().apply {
            addItems(ArrayList(circles))
            this.context = requireContext()
        }
        binding.recyclerView.setup().adapter = circleAdapter

        circleAdapter.onItemClick = { _, item, _ ->
            viewModel.fetchCircleProviderList(item.id.toString())
        }

        if(circles.isEmpty()){
            binding.layoutNoDataFound.root.show()
        }else binding.layoutNoDataFound.root.hide()
    }


    //UI Stuff
    private fun contentLayoutVisibility(visible: Boolean = true) {
        binding.run {
            if (visible) {
                this.recyclerView.show()
                this.layoutProgress.rootLayout.hide()
            } else {
                this.recyclerView.hide()
                this.layoutProgress.rootLayout.show()
                this.layoutNoDataFound.root.hide()
            }
        }
    }

    companion object {
        const val PROVIDER_KEY = "provider_key"
    }


}