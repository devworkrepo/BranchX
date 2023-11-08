package com.branchx.agent.ui.fragment.bill_recharge

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.Provider
import com.branchx.agent.data.response.FetchBillResponse
import com.branchx.agent.databinding.FragmentBillPayBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.ui.activity.RechargeBillPayActivity
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.fragment.main.ServiceType
import com.branchx.agent.ui.fragment.main.getServiceName
import com.branchx.agent.ui.viewmodel.bill_recharge.BillPayViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BillPayFragment : BaseFragment<FragmentBillPayBinding>(R.layout.fragment_bill_pay) {
    private val viewModel : BillPayViewModel by viewModels()

    private var activityInstance : RechargeBillPayActivity? =null
    private var serviceType : ServiceType? =null
    private var provider : Provider? =null

    @Inject
    lateinit var appPreference: AppPreference

    //dialogs
    private var billPaymentProgressDg : Dialog? = null
    private var fetchBillProgressDg : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        provider = arguments?.getParcelable(ProviderFragment.PROVIDER_KEY)



    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.serviceType = serviceType

        setupUIStuff()

        setupToolbar(binding.toolbar.toolbar, getServiceName(serviceType))

        subscribeObservers()

        binding.tvWalletBalance.text = appPreference.user.mainBalance


    }

    private fun subscribeObservers(){

        //FETCH BILL INFO OBSERVER
        viewModel.fetchBillObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    AppUtil.logger("OnLoading")
                    fetchBillProgressDg = AppDialog.progress(
                        requireActivity(),
                        "Fetching bill info..."
                    )
                }
                is Resource.Success -> {
                    fetchBillProgressDg?.dismiss()
                    AppUtil.logger("OnSuccess")
                    onSuccessResponseFetchBill(it.data)
                }
                is Resource.Failure -> {
                    fetchBillProgressDg?.dismiss()
                    AppUtil.logger("OnFailure")
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

        //BILL PAYMENT OBSERVERS

        viewModel.billPaymentObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    billPaymentProgressDg = AppDialog.transactionProgress(requireActivity())
                }
                is Resource.Success -> {
                    billPaymentProgressDg?.dismiss()
                    findNavController().navigate(R.id.action_billPaymentFragment_to_rechargeSuccessFragment,
                    bundleOf(
                        AppConstants.RESPONSE_KEY to it.data,AppConstants.ORIGIN_KEY to ORIGIN,
                        AppConstants.AMOUNT_KEY to viewModel.amount,
                        AppConstants.PROVIDER_NAME_KEY to provider!!.providerName,
                        AppConstants.CA_NUMBER_KEY to viewModel.caOrBillNumber,
                        AppConstants.MOBILE_NUMBER_KEY to viewModel.mobileNumber
                    ))
                }
                is Resource.Failure -> {
                    billPaymentProgressDg?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

    }

    private fun onSuccessResponseFetchBill(data: FetchBillResponse) {
        when(data.status){
            1 -> {
                binding.let {
                    viewModel.eRoNoTF = data.eRoNoTF
                    setLayoutVisibility()
                    if (data.eRoNoTF == 1) it.edEroNumber.show()
                    else it.llEroNumber.hide()

                    it.tvDueDate.text = data.dueDate
                    it.tvBillNumber.text = data.billNumber

                    it.edAmount.setText(data.dueAmount)
                    it.tvConsumerName.text = data.billerName

                    viewModel.customerName = data.billerName
                    viewModel.dueDate = data.dueDate

                }
            }
            else -> AppDialog.failure(requireActivity(), data.message)
        }
    }


    private fun setupUIStuff(){

        activityInstance = (activity as RechargeBillPayActivity)
        serviceType = activityInstance?.serviceType
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.operator = provider?.id

        binding.let {
            setLayoutVisibility(false)

            //titles
            it.tvTitle.text = provider?.providerName
            it.tvSubTitle.text = serviceType?.strName()


        }


    }


   private fun setLayoutVisibility(visible: Boolean = true){
        binding.let {
            if(visible){
                it.llPaymentFieldLayout.show()
                it.cvFetchDetail.show()
                it.btnMakePayment.show()
            }

            else{
                it.llPaymentFieldLayout.hide()
                it.cvFetchDetail.hide()
                it.btnMakePayment.hide()
                it.llEroNumber.hide()
            }
        }
    }

    companion object{
        const val ORIGIN = "bill_pay"
    }
}