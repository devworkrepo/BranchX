package com.branchx.agent.ui.fragment.main


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.User
import com.branchx.agent.databinding.FragmentHomeBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.activity.*
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.dialog.DialogNotification
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {


    @Inject
    lateinit var appPreference: AppPreference

    private val viewModel: HomeViewModel by viewModels()


    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = appPreference.user

        binding.let {
            it.tvMainBalance.text = user.mainBalance
            it.tvAepsBalance.text = user.aepsBalance
        }
        setUpService()


        binding.btnTopUp.setOnClickListener {
            activity?.launchActivity(FundRequestActivity::class.java)
        }

        binding.btnRefreshBalance.setOnClickListener {
            viewModel.fetchBalance(true)
        }



        subscribeObservers()




    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchBalance()
    }

    private fun subscribeObservers() {
        viewModel.balanceObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Fetching Balance...")
                }
                is Resource.Success -> {
                    val mUser = appPreference.user
                    mUser.mainBalance = it.data.mainBalance
                    mUser.aepsBalance = it.data.aepsBalance
                    mUser.matmBalance = it.data.matmBalance
                    appPreference.user = mUser
                    binding.tvMainBalance.text = it.data.mainBalance
                    binding.tvAepsBalance.text = it.data.aepsBalance
                    binding.tvMatmBalance.text = it.data.matmBalance
                    progressDialog?.dismiss()
                }
                is Resource.Failure -> {
                    activity?.handleNetworkFailure(it.exception)
                    progressDialog?.dismiss()
                }
            }
        })


        viewModel.notificationObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val notification = it.data.notifications
                    if (it.data.status == 1
                        && notification != null
                        && notification.isNotEmpty()
                    ) {

                        binding.cvNotification.show()
                        if(it.data.count == 1) {
                            binding.tvNotification.text = "${it.data.count} new notification"
                        } else if(it.data.count == 0){
                            binding.tvNotification.text = "No new notification"
                        } else {
                            binding.tvNotification.text = "${it.data.count} new notifications"
                        }

                        binding.cvNotification.setOnClickListener{
                            DialogNotification(requireContext(),notification)
                        }

                    } else {
                        binding.cvNotification.hide()
                    }
                }
                is Resource.Failure -> {
                    binding.cvNotification.hide()
                }
            }
        })
    }

    private fun setUpService() {
        binding.let {

            //ACTIVITIES
            val rechargeActivity = RechargeBillPayActivity::class.java
            val dmtActivity = DMTActivity::class.java

            //SERVICE_TYPE
            val prepaid = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.PREPAID)
            val postpaid = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.POSTPAID)
            val dth = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.DTH)
            val electricity = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.ELECTRICITY)
            val gas = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.GAS)
            val water = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.WATER)
            val broadband = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.BROADBAND)
            val landLine = bundleOf(AppConstants.SERVICE_TYPE_KEY to ServiceType.LANDLINE)

            it.clMatm.setOnClickListener {
                activity?.launchActivity(
                    MatmActivity::class.java, bundleOf(
                        AppConstants.AEPS_SERVICE_TYPE to AepsServiceType.AEPS
                    )
                )
            }

            it.cvAeps.setOnClickListener {
                activity?.launchActivity(
                    AepsActivity::class.java, bundleOf(
                        AppConstants.AEPS_SERVICE_TYPE to AepsServiceType.AEPS
                    )
                )
            }
            it.cvAadhaarPay.setOnClickListener {
                activity?.launchActivity(
                    AepsActivity::class.java, bundleOf(
                        AppConstants.AEPS_SERVICE_TYPE to AepsServiceType.AADHAAR_PAY
                    )
                )
            }

            it.cvPrepaid.setOnClickListener {
                activity?.launchActivity(rechargeActivity, prepaid)
            }
            it.cvPostpaid.setOnClickListener {
                activity?.launchActivity(rechargeActivity, postpaid)
            }
            it.cvDth.setOnClickListener {
                activity?.launchActivity(rechargeActivity, dth)
            }
            it.cvWater.setOnClickListener {
                activity?.launchActivity(rechargeActivity, water)
            }
            it.cvGas.setOnClickListener {
                activity?.launchActivity(rechargeActivity, gas)
            }
            it.cvBroadband.setOnClickListener {
                activity?.launchActivity(rechargeActivity, broadband)
            }
            it.cvLandLine.setOnClickListener {
                activity?.launchActivity(rechargeActivity, landLine)
            }
            it.cvElectricity.setOnClickListener {
                activity?.launchActivity(rechargeActivity, electricity)
            }
            it.cvDmt1.setOnClickListener {
                activity?.launchActivity(
                    dmtActivity,
                    bundleOf(AppConstants.DMT_TYPE_KEY to DmtType.DMT_TWO)
                )
            }

            it.clNeoBanking.setOnClickListener {
                activity?.launchActivity(NeoBankingActivity::class.java)
            }

        }
    }

    companion object {

        fun newInstance() = HomeFragment()

        const val DMT_TYPE_ONE = "dmt_one"
        const val DMT_TYPE_TWO = "dmt_two"
    }

}

enum class ServiceType {
    PREPAID, POSTPAID, DTH, DATA_CARD, WATER, GAS, ELECTRICITY, BROADBAND, LANDLINE
}

fun getServiceName(it: ServiceType?): String {
    return when (it) {
        ServiceType.PREPAID -> "Prepaid"
        ServiceType.POSTPAID -> "Postpaid"
        ServiceType.DATA_CARD -> "Data Card"
        ServiceType.WATER -> "Water"
        ServiceType.GAS -> "Gas"
        ServiceType.ELECTRICITY -> "Electricity"
        ServiceType.BROADBAND -> "Broadband"
        ServiceType.LANDLINE -> "LandLine"
        else -> ""
    }
}


enum class AepsServiceType {
    AEPS, AADHAAR_PAY
}