package com.branchx.agent.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentR2rTransferBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.ViewUtil
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.R2RTransferViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class R2RTransferFragment :
    BaseFragment<FragmentR2rTransferBinding>(R.layout.fragment_r2r_transfer) {

    private val viewModel: R2RTransferViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        subscribeObserver()

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilMobileNumber,
            binding.tilAmount,
            binding.tilOtp,
        )

        binding.btnProceed.setOnClickListener {
            if (!inputValidationForRequestingOtp()) return@setOnClickListener

            when (viewModel.actionType) {
                R2RTransferViewModel.ActionType.REQUEST_OTP -> viewModel.requestOtp()
                R2RTransferViewModel.ActionType.PROCEED -> viewModel.proceedTransaction()
            }

        }
    }

    private fun subscribeObserver() {

        viewModel.requestOtpObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireActivity(), "Request Otp...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        binding.btnProceed.text = "Confirm & Proceed"
                        binding.cardOtp.show()
                        binding.edMobileNumber.isEnabled = false
                        binding.edAmount.isEnabled = false
                        viewModel.actionType = R2RTransferViewModel.ActionType.PROCEED
                        viewModel.transactionId = it.data.transid.toString()
                        activity?.makeToast(it.data.message)
                    } else AppDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

        viewModel.proceedTransactionObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireActivity(), "Request Otp...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message, goBack = true)
                    } else AppDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })
    }

    private fun inputValidationForRequestingOtp(): Boolean {
        var isValid = true

        if (viewModel.mobileNumber.length < 10) {
            binding.tilMobileNumber.apply {
                error = "Enter 10 digits mobile number"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilMobileNumber.isErrorEnabled = false


        //todo remove 1 rs validation and replace with 1000
        if (viewModel.amount.isEmpty() || viewModel.amount.toDouble() < 1.0) {
            binding.tilAmount.apply {
                error = "Enter amount 1000 to 10000"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilAmount.isErrorEnabled = false

        if (viewModel.actionType == R2RTransferViewModel.ActionType.PROCEED) {
            if (viewModel.otp.length != 4) {
                binding.tilOtp.apply {
                    error = "Enter 4 digits otp"
                    isErrorEnabled = true
                    isValid = false
                }
            } else binding.tilOtp.isErrorEnabled = false
        }
        return isValid
    }


    companion object {
        fun newInstance() = R2RTransferFragment()
    }
}