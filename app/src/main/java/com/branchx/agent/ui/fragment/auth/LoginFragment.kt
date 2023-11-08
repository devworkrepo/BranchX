package com.branchx.agent.ui.fragment.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.data.response.LoginResponse
import com.branchx.agent.databinding.FragmentLoginBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.ui.activity.MainActivity
import com.branchx.agent.ui.activity.Policy
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment() : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private var passwordShow:Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        onAcceptPolicy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.deviceid = AppUtil.getDeviceId(requireActivity())

//        binding.tvRegister.setOnClickListener {
//            //findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
//
//            //todo registration link should change
//            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pay.mymego.in/Login/Registration"))
//            startActivity(browserIntent)
//        }

        binding.hideShowPasswordIvLa.setOnClickListener{

            if(passwordShow){
                binding.hideShowPasswordIvLa.setImageResource(R.drawable.eye)
                binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordShow = false
            }else{
                binding.hideShowPasswordIvLa.setImageResource(R.drawable.hidden)
                binding.edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordShow = true
            }
        }

        binding.signUpText.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://partner.branchx.in/Login/Registration"))
            startActivity(browserIntent)
        }



        subscribeObserver()
    }

//    override fun onResume() {
//        super.onResume()
//        hideShowPassword_Iv_La.setOnClickListener {
//            if(passwordShow){
//                hideShowPassword_Iv_La.setImageResource(R.drawable.eye)
//                ed_password.transformationMethod = PasswordTransformationMethod.getInstance()
//                passwordShow = false
//            }else{
//                hideShowPassword_Iv_La.setImageResource(R.drawable.hidden)
//                ed_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
//                passwordShow = true
//            }
//        }
//    }

    private fun onAcceptPolicy() {
        binding.tvRegister.setOnClickListener{
            val intent = Intent(activity, Policy::class.java)
            startActivity(intent)
        }
    }

    private fun subscribeObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    onLoginSuccess(it.data)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.forgotPasswordObs.observe(viewLifecycleOwner, Observer {

                when (it) {
                    is Resource.Loading ->{
                        progressDialog = AppDialog.progress(requireActivity())
                    }
                    is Resource.Success ->{
                        progressDialog?.dismiss()
                        if(it.data.status == 1){
                            AppDialog.success(requireActivity(),it.data.message)
                        }else AppDialog.failure(requireActivity(),it.data.message)
                    }
                    is Resource.Failure ->{
                        progressDialog?.dismiss()
                        AppDialog.failure(requireActivity(),it.exception.message.toString())
                    }
                }

        })
    }

    private fun onLoginSuccess(data: LoginResponse) {

        when (data.status) {
            1 -> {
                activity?.apply {
                    val loginIntent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
            }
            3 -> {//DEVICE OTP VERIFY
                val direction = LoginFragmentDirections.actionLoginFragmentToVerifyDeviceFragment(
                    viewModel.mobileNumber, viewModel.password, data.message, false
                )
                findNavController().navigate(direction)
            }
            4 -> {//LOGIN OTP VERIFY
                val direction = LoginFragmentDirections.actionLoginFragmentToVerifyDeviceFragment(
                    viewModel.mobileNumber, viewModel.password, data.message, true
                )
                findNavController().navigate(direction)
            }
            else -> {
//                binding.tvErrorMessage.let {
//                    it.text = data.message
//                    it.show()
//                }
                AppDialog.failure(requireActivity(),data.message)
            }
        }

    }

    companion object {
        fun newInstance() = LoginFragment()
    }

}