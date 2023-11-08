package com.branchx.agent.ui.fragment.main

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentChangePasswordBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.ViewUtil
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.auth.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password) {

    private val viewModel: ChangePasswordViewModel by viewModels()
    private var otpVerifyDialog: Dialog? = null
    var otpDialogBankOnDismiss = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        subscribeObserver()

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilPassword,
            binding.tilConfirmPassword,
            binding.tilConfirmPassword,
        )

        binding.btnChangePin.setOnClickListener {
            if (!validateChangePassword()) return@setOnClickListener
            viewModel.changePassword()
        }
    }

    private fun subscribeObserver() {

        viewModel.onPasswordChangeObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireActivity(), "Submitting...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    otpDialogBankOnDismiss = false
                    otpVerifyDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message,true)
                    } else AppDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })
    }
    private fun validateChangePassword(): Boolean {
        var isValid = true

        /*if (!AppUtil.isValidPasswordFormat(viewModel.currentPassword)) {
            binding.tilPassword.apply {
                error = "Enter valid password with rules"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilPassword.isErrorEnabled = false*/

        if (viewModel.currentPassword.length <5) {
            binding.tilPassword.apply {
                error = "Enter valid password with rules"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilPassword.isErrorEnabled = false


        if (!AppUtil.isValidPasswordFormat(viewModel.newPassword)) {
            binding.tilNewPassword.apply {
                error = "Enter valid password with rules"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilNewPassword.isErrorEnabled = false

        if (viewModel.newPassword != viewModel.confirmPassword) {
            binding.tilConfirmPassword.apply {
                error = "Confirm password doesn't match with new password"
                isErrorEnabled = true
                isValid = false
            }
        } else binding.tilConfirmPassword.isErrorEnabled = false


        return isValid
    }


    companion object {
        fun newInstance() = ChangePasswordFragment()
    }
}