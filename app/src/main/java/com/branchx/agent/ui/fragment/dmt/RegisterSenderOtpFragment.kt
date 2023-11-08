package com.branchx.agent.ui.fragment.dmt

import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentSenderRegisterOtpBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.makeToast
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.SenderRegisterOtpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSenderOtpFragment :
    BaseFragment<FragmentSenderRegisterOtpBinding>(R.layout.fragment_sender_register_otp) {

    private val viewModel: SenderRegisterOtpViewModel by viewModels()

    private lateinit var dmtType: DmtType
    private lateinit var senderMobileNumber: String
    private lateinit var senderFirstName: String
    private lateinit var senderLastName: String
    private lateinit var responseMessage: String


    private var registerSenderOtpDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: RegisterSenderOtpFragmentArgs by navArgs()
        dmtType = args.dmtType
        senderMobileNumber = args.senderMobile
        responseMessage = args.responseMessage
        senderFirstName = args.firstName
        senderLastName = args.lastName


    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.customToolbar.let {
            it.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            it.setNavigationOnClickListener { activity?.onBackPressed() }
        }
        binding.toolbar.tvTitle.text = getString(R.string.dmt_one)
        binding.toolbar.ivLogo.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_rupee_indian
            )
        )


        viewModel.dmtType = dmtType
        viewModel.senderMobileNumber = senderMobileNumber
        viewModel.firstName = senderFirstName
        viewModel.lastName = senderLastName

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.tvResponseMessage.text = responseMessage


        subscribeObservers()

        binding.btnResendOtp.setOnClickListener{
            viewModel.resendOtp()
        }

    }



    private fun subscribeObservers() {
        viewModel.registerSenderOtpObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    registerSenderOtpDialog =
                        AppDialog.progress(requireContext(), "Submitting Request...")
                }
                is Resource.Failure -> {
                    registerSenderOtpDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    registerSenderOtpDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message).apply {
                            setOnDismissListener {
                                gotoSearchSenderFragment()
                            }
                        }
                    } else AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }


        viewModel.onResendOtpObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    registerSenderOtpDialog =
                        AppDialog.progress(requireContext(), "Request Otp")
                }
                is Resource.Failure -> {
                    registerSenderOtpDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    registerSenderOtpDialog?.dismiss()
                    activity?.makeToast(it.data.message)
                }
            }
        }

    }


    private fun gotoSearchSenderFragment() {
        findNavController().navigate(
            RegisterSenderOtpFragmentDirections
                .actionRegisterSenderOtpFragmentToSenderSearchFragment(
                    viewModel.senderMobileNumber,
                    dmtType))
    }

}