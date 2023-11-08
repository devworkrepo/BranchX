package com.branchx.agent.ui.fragment.dmt

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.branchx.agent.DmtNavGraphArgs
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.databinding.FragmentSenderSearchBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.SenderSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SenderSearchFragment :
    BaseFragment<FragmentSenderSearchBinding>(R.layout.fragment_sender_search) {


    @Inject
    lateinit var appPreference: AppPreference
    private val viewModel: SenderSearchViewModel by viewModels()
    private lateinit var dmtType: DmtType
    private var senderMobileNumber : String = ""
    private var senderFirstName : String = ""
    private var senderLastName : String = ""
    private var monthlyLimit : String = ""


    private var senderVerifyingProgress: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: DmtNavGraphArgs? by navArgs()
        args?.let { dmtType = it.dmtType }

       val args2 : SenderSearchFragmentArgs? by navArgs()
        args2?.let {
            dmtType = it.dmtType
            senderMobileNumber = it.senderMobileNumber
            senderFirstName = it.firstName
            senderLastName = it.lastName
            monthlyLimit = it.monthlyLimit
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.dmtType = dmtType
        viewModel.senderMobileNumber = senderMobileNumber ?: ""
        viewModel.senderFirstName = senderFirstName?: ""
        viewModel.senderLastName = senderLastName?: ""
        viewModel.monthlyLimit = monthlyLimit?: ""


        binding.toolbar.customToolbar.let {
            it.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            it.setNavigationOnClickListener { activity?.onBackPressed() }
        }
        binding.toolbar.tvTitle.text = getString(R.string.dmt_one)
//        binding.toolbar.ivLogo.setImageDrawable(
//            ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.ic_rupee_indian
//            )
//        )

        if(viewModel.senderMobileNumber.length == 10){
            viewModel.onSearchButtonClick()
            senderMobileNumber = ""
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.senderVerificationObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                    senderVerifyingProgress =
                        AppDialog.progress(requireContext(), "Verifying...")
                }

                is Resource.Success -> {
                    senderVerifyingProgress?.dismiss()
                    onSuccessVerifySenderResponse(it.data)

                    viewModel.senderMobileNumber = ""
                    senderMobileNumber = ""
                }

                is Resource.Failure -> {
                    senderVerifyingProgress?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun onSuccessVerifySenderResponse(data: SenderInfo) {
        when (data.status) {
            1 -> {
                data.mobileNumber = viewModel.senderMobileNumber
                data.firstName = viewModel.senderFirstName
                data.lastName = viewModel.senderLastName
                data.remainingBalance = viewModel.monthlyLimit
                findNavController().navigate(
                    R.id.action_senderSearchFragment_to_beneficiaryListFragment,
                    BeneficiaryListFragmentArgs(dmtType, data, data.firstName.toString(),data.lastName.toString(), data.remainingBalance.toString()).toBundle()
                )
            }
            11 -> {
                findNavController().navigate(
                    R.id.action_senderSearchFragment_to_registerSenderFragment,
                    RegisterSenderFragmentArgs(dmtType, viewModel.senderMobileNumber,
                    senderFirstName,senderLastName).toBundle()
                )
            }
            12 -> {
                findNavController().navigate(
                    R.id.action_senderSearchFragment_to_registerSenderOtpFragment,
                    RegisterSenderOtpFragmentArgs(
                        dmtType,
                        data.message,
                        viewModel.senderMobileNumber,senderFirstName,senderLastName
                    ).toBundle()
                )
            }
            else -> AppDialog.failure(requireActivity(), data.message)
        }
    }


}