package com.branchx.agent.ui.fragment.dmt

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.databinding.FragmentBeneficiaryListBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.*
import com.branchx.agent.ui.adapter.BeneficiaryListAdapter
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.dialog.ConfirmDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.BeneficiaryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BeneficiaryListFragment :
    BaseFragment<FragmentBeneficiaryListBinding>(R.layout.fragment_beneficiary_list) {

    private val viewModel: BeneficiaryListViewModel by viewModels()

    @Inject
    lateinit var appPreference: AppPreference
    private lateinit var dmtType: DmtType
    private lateinit var senderInfo: SenderInfo
    lateinit var senderMobileNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: BeneficiaryListFragmentArgs by navArgs()
        dmtType = args.dmtType
        senderInfo = args.senderInfo
        viewModel.senderInfoObs.value = args.senderInfo

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this
        viewModel.dmtType = dmtType
        viewModel.mobileNumber = senderInfo.mobileNumber.toString()
        viewModel.senderName = senderInfo.name.toString()
        viewModel.senderInfoObs.value = senderInfo
        viewModel.monthlyLimit = senderInfo.remainingBalance.toString()
       // viewModel.senderInfo.value?.name = senderInfo.name


        //setup toolbar
        val toolbarAmount = appPreference.user.mainBalance
        val toolbarTitle = requireContext().resources.getString(R.string.beneficiary)
        setupCustomToolbarTitleAmount(binding.toolbar, toolbarAmount, toolbarTitle)

        binding.fabAddBeneficiary.setOnClickListener {
            val senderName = senderInfo.firstName + senderInfo.lastName
            val senderInfo : SenderInfo? = viewModel.senderInfoObs.value
            senderInfo?.let {
                findNavController().navigate(
                    R.id.action_beneficiaryListFragment_to_addBeneficiaryFragment,
                    AddBeneficiaryFragmentArgs(
                        viewModel.dmtType,
                        viewModel.senderName,
                        it.mobileNumber.orEmpty()
                    ).toBundle()
                )
            }
        }

        viewModel.getBeneficiaryList()
        subscribeObservers()


    }

    private fun subscribeObservers() {
        viewModel.beneficiaryListObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> setupVisibility(false)
                is Resource.Success -> {
                    if (it.data.status == 1) {
                        setupVisibility()
                        setupList(it.data.beneficiary)
                        setupUIStuff()
                    } else setupVisibility(errorMessage = it.data.message)
                }
                is Resource.Failure -> {
                    setupVisibility(errorMessage = it.exception.message.toString())
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }


        viewModel.beneficiaryDeleteObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> progressDialog = AppDialog.progress(requireActivity())
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message).apply {
                            setOnDismissListener {
                               viewModel.getBeneficiaryList()
                            }
                        }
                    } else AppDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.beneficiaryVerifyObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> progressDialog = AppDialog.progress(requireActivity())
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message)
                    } else if(it.data.status == 3){
                        AppDialog.pending(requireActivity(), it.data.message)
                    } else {
                        AppDialog.failure(requireActivity(), it.data.message)
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun setupList(dmtOneBeneficiary: List<BeneficiaryInfo>) {

        binding.recyclerView.setup().adapter =
            BeneficiaryListAdapter(requireContext(), appPreference.user.mainBalance,viewModel).apply {
                context = requireContext()
                addItems(ArrayList(dmtOneBeneficiary))
                this.onTransferButtonClickCallback = { model, strAmount ->

                    viewModel.senderInfoObs.value.let {
                        findNavController().navigate(
                            R.id.action_beneficiaryListFragment_to_moneyTransactionFragment,
                            MoneyTransactionFragmentArgs(
                                viewModel.dmtType,
                                strAmount,
                                viewModel.senderInfoObs.value!!,
                                model
                            ).toBundle()
                        )
                    }
                }

                this.deleteBeneListener = {
                    viewModel.beneficiaryId = it.id
                    viewModel.beneficiary = it
                    deleteBeneficiary()
                }

                this.verifyBeneListener = {
                    viewModel.beneficiary = it
                    viewModel.beneficiaryId = it.beneficiaryId.toString()
                    verifyBeneficiary()

                }
            }
    }

    private fun setupUIStuff() {
        viewModel.senderInfoObs.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvSenderName.text = viewModel.senderName
                binding.tvSenderMobile.text = viewModel.senderInfoObs.value?.mobileNumber
                binding.tvSenderLimit.text = viewModel.monthlyLimit
            }
        }
    }

//     private fun setupUI() {
//        viewModel.senderInformation.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Loading -> progressDialog = AppDialog.progress(requireActivity())
//                is Resource.Success -> {
//                    progressDialog?.dismiss()
//                    if (it.data.status == 1) {
//                        Log.d("Nameeee",viewModel.senderInfo.value?.name.toString())
//                       // binding.tvSenderName.text = viewModel.senderName
//                        binding.tvSenderMobile.text = senderInfo.mobileNumber
//                        binding.tvSenderLimit.text = senderInfo.remainingBalance
//                    } else AppDialog.failure(requireActivity(), it.data.message)
//                }
////                is Resource.Failure -> {
////                    progressDialog?.dismiss()
////                    activity?.handleNetworkFailure(it.exception)
////                }
//            }
//        }
//    }

    private fun setupVisibility(visible: Boolean = true, errorMessage: String? = null) {
        binding.let {
            if (visible) {
                it.layoutProgress.root.hide()
                it.contentLayout.show()

                errorMessage?.let { m ->
                    it.tvErrorMessage.show()
                    it.tvErrorMessage.text = m
                } ?: run {
                    it.tvErrorMessage.hide()
                }
            } else {
                it.layoutProgress.root.show()
                it.contentLayout.hide()
                it.tvErrorMessage.hide()
            }
        }
    }

    private fun deleteBeneficiary() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Would you like to delete this beneficiary?")
        builder.setCancelable(false)
        builder.setPositiveButton("Done") {
                dialog, which ->  viewModel.deleteDmt2Beneficiary()
        }
        builder.setNegativeButton("Cancel") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun verifyBeneficiary() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Would you like to verify the account details? Charges: ₹3 including GST")
        builder.setCancelable(false)
        builder.setPositiveButton("Done") {
                dialog, which ->  viewModel.verifyBeneficiary()
        }
        builder.setNegativeButton("Cancel") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}