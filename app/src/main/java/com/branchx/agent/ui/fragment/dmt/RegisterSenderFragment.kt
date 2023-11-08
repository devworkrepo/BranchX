package com.branchx.agent.ui.fragment.dmt

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentSenderRegisterBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.SenderRegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSenderFragment :
    BaseFragment<FragmentSenderRegisterBinding>(R.layout.fragment_sender_register) {

    private val viewModel: SenderRegisterViewModel by viewModels()

    private lateinit var dmtType: DmtType
    private lateinit var senderMobileNumber: String
    private lateinit var senderFirstName: String
    private lateinit var senderLastName: String

    private var registerSenderDialog : Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: RegisterSenderFragmentArgs by navArgs()
        dmtType = args.dmtType
        senderMobileNumber = args.senderMobile
        senderFirstName = args.firstName
        senderLastName = args.lastName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.toolbar.ivLogo.visibility = View.GONE



        viewModel.dmtType = dmtType
        viewModel.senderMobileNumber = senderMobileNumber
        viewModel.senderFirstName = senderFirstName
        viewModel.senderLastName = senderLastName

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        subscribeObservers()

    }


    private fun subscribeObservers() {
        viewModel.registerSenderObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    registerSenderDialog = AppDialog.progress(requireContext(), "Requesting Otp..")
                }
                is Resource.Failure -> {
                    registerSenderDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    registerSenderDialog?.dismiss()
                    if (it.data.status == 1) {
                        findNavController().navigate(
                            R.id.action_registerSenderFragment_to_registerSenderOtpFragment,
                            RegisterSenderOtpFragmentArgs(
                                dmtType,
                                it.data.message,
                                viewModel.senderMobileNumber,viewModel.senderFirstName.toString(),
                                viewModel.senderLastName.toString()
                            ).toBundle()
                        )
                    }
                    else AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }

    }


}