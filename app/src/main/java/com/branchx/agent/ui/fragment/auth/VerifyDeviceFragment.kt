package com.branchx.agent.ui.fragment.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentVerifyBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.auth.DeviceVerifyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyDeviceFragment : BaseFragment<FragmentVerifyBinding>(R.layout.fragment_verify) {

    private val args: VerifyDeviceFragmentArgs by navArgs()
    private val viewModel: DeviceVerifyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mobileNumber = args.mobileNumber
        viewModel.password = args.password

        viewModel.isLoginOtpRequest = args.isLoginOtpRequest

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.tvMessage2.text = args.message

        binding.ibBack.setOnClickListener{
            activity?.onBackPressed()
        }

        viewModel.verificationOtpRequest()

        subscribeObserver()

    }

    private fun subscribeObserver() {

        //GENERATE OTP FOR DEVICE VERIFICATION
        viewModel.generateOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.mainConstraintLayout.hide()
                    binding.layoutProgress.root.show()
                }
                is Resource.Failure -> {
                    binding.mainConstraintLayout.show()
                    binding.layoutProgress.root.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    binding.mainConstraintLayout.show()
                    binding.layoutProgress.root.hide()

                    binding.tvMessage2.text = it.data.message+" "+viewModel.mobileNumber

                    if (it.data.status != 1)
                        AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }


        //VERIFYING DEVICE VIA OTP
        viewModel.verifyDeviceOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.mainConstraintLayout.hide()
                    binding.layoutProgress.root.show()
                }
                is Resource.Failure -> {
                    binding.mainConstraintLayout.show()
                    binding.layoutProgress.root.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    binding.mainConstraintLayout.show()
                    binding.layoutProgress.root.hide()

                    if (it.data.status == 1)
                        AppDialog.success(requireActivity(), it.data.message, true)
                    else AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }
    }
}