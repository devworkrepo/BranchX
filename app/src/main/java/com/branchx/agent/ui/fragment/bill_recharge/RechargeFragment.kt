package com.branchx.agent.ui.fragment.bill_recharge

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.viewModels
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.Provider
import com.branchx.agent.data.response.CommonResponse
import com.branchx.agent.databinding.FragmentRechargeBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.gotoMainActivity
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.setupToolbar
import com.branchx.agent.helper.extns.strName
import com.branchx.agent.ui.activity.RechargeBillPayActivity
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.fragment.main.getServiceName
import com.branchx.agent.ui.viewmodel.bill_recharge.RechargeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RechargeFragment : BaseFragment<FragmentRechargeBinding>(R.layout.fragment_recharge) {
    private val viewModel: RechargeViewModel by viewModels()
    private var activityInstance: RechargeBillPayActivity? = null
    private var serviceType: ServiceType? = null
    private var provider: Provider? = null

    @Inject
    lateinit var appPreference: AppPreference

    //dialogs
    private var rechargeProgressDg: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        provider = arguments?.getParcelable(ProviderFragment.PROVIDER_KEY)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        setupUIStuff()

        setupToolbar(binding.toolbar.toolbar, getServiceName(serviceType))

        subscribeObservers()

        binding.tvWalletBalance.text = appPreference.user.mainBalance
//

    }

    private fun subscribeObservers() {
        viewModel.rechargeObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    rechargeProgressDg = AppDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    rechargeProgressDg?.dismiss()
                    onRechargeSuccessResponse(it.data)

                }
                is Resource.Failure -> {
                    rechargeProgressDg?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun onRechargeSuccessResponse(data: CommonResponse) {
        if (data.status == 1)
            AppDialog.success(requireActivity(), data.message).apply {
                setOnDismissListener { activity?.gotoMainActivity() }
            }
        else AppDialog.failure(requireActivity(), data.message)

    }

    private fun setupUIStuff() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        activityInstance = (activity as RechargeBillPayActivity)
        serviceType = activityInstance?.serviceType
        viewModel.serviceType = serviceType

        binding.let {
            provider?.let { provider ->
                viewModel.operator = provider.id
                it.tvTitle.text = provider.providerName
                it.tvSubTitle.text = serviceType?.strName()
            }
        }

        if(serviceType == ServiceType.DTH){
            binding.tvNumberTitle.text = "DTH Number"
        }
        else{
            binding.tvNumberTitle.text = "Mobile Number"
        }

        if(serviceType == ServiceType.DTH){
            binding.edNumber.filters = arrayOf(InputFilter.LengthFilter(14))
        }
        else{
            binding.edNumber.filters = arrayOf(InputFilter.LengthFilter(10))
        }

    }

    companion object{
        const val ORIGIN = "recharge"
    }
}


