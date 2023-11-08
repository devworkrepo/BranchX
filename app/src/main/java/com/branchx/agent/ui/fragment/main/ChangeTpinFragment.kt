package com.branchx.agent.ui.fragment.main

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.branchx.agent.R
import com.branchx.agent.base.BaseFragment
import com.branchx.agent.data.response.TPinModel
import com.branchx.agent.data.response.TPinResponse
import com.branchx.agent.databinding.FragmentChangeTpinBinding
import com.branchx.agent.databinding.ViewBalanceOtpLayoutBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.viewmodel.neo_banking.NeoBankingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeTpinFragment : BaseFragment() {

    private lateinit var mBinding: FragmentChangeTpinBinding
    private val neoViewModel: NeoBankingViewModel by viewModels()
    var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding = FragmentChangeTpinBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {

        mBinding.mcvSendOtp.setOnClickListener {
            if (mBinding.etNewTpin.text.toString()
                    .isEmpty() || mBinding.etConfirmTpin.text.toString().isEmpty()
            ) {
                Toast.makeText(requireActivity(), "Field cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (mBinding.etNewTpin.text.toString() != mBinding.etConfirmTpin.text.toString()) {
                Toast.makeText(requireActivity(), "Fields must be same", Toast.LENGTH_SHORT).show()
            } else if (mBinding.etNewTpin.text.toString().length < 4 || mBinding.etConfirmTpin.text.toString().length < 4) {
                Toast.makeText(requireActivity(), "Fields must be 4 digits", Toast.LENGTH_SHORT)
                    .show()
            } else {
                neoViewModel.getTPinOtp()
                setUpOtpObserver()
            }

        }


    }

    private fun setUpOtpObserver() {
        neoViewModel.getTPinOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    Log.d(TAG, "TPIN_OTP:: ${it.data}")
                    onSuccess(it.data, false)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }

            }
        }
    }

    private fun setUpChangeTPinObserver(bottomSheetDialog: BottomSheetDialog) {
        neoViewModel.generateTPin.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    bottomSheetDialog.dismiss()
                    Log.d(TAG, "CHANGE_TPIN:: ${it.data.message}")
                    onSuccess(it.data, isChangeTPin = true)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }

            }
        }
    }


    private fun onSuccess(data: TPinResponse, isChangeTPin: Boolean) {

        Log.d(TAG, "TPIN_RESPONSE:: $data")

        when (data.status) {
            0 -> {
                if (isChangeTPin) {
                    data.message.let {
                        AppDialog.failure(requireActivity(), it)
                    }
                } else {
                    data.message.let {
                        AppDialog.failure(requireActivity(), it)
                    }
                    Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                }
            }

            1 -> {
                if (isChangeTPin) {
                    AppDialog.success(requireActivity(), data.message, true)
                    Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                } else {
                    showOtpBottomSheetDialog()
                    Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                data.message.let { AppDialog.failure(requireActivity(), it) }
            }
        }

    }


    private fun showOtpBottomSheetDialog() {
        val bottomSheetBinding =
            ViewBalanceOtpLayoutBinding.inflate(LayoutInflater.from(context), null, false)

        BottomSheetDialog(requireActivity(), R.style.MyBottomSheetDialogTheme).apply {
            if (!this.isShowing) {
                this.setCancelable(false)
                this.setContentView(bottomSheetBinding.root)
                this.show()

                var otp = ""
                bottomSheetBinding.let { sheet ->
                    Log.d(TAG, "OTP:: $otp")
                    sheet.mcvProceed.setOnClickListener {
                        otp = sheet.pinview.text.toString()
                        if (otp.length == 6) {
                            TPinModel(
                                mBinding.etNewTpin.text.toString(),
                                mBinding.etNewTpin.text.toString(),
                                otp
                            ).let {
                                neoViewModel.changeTPin(it)
                                setUpChangeTPinObserver(this)
                            }


                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Enter Valid otp",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    sheet.ivClose.setOnClickListener {
                        dismiss()
                    }
                }
            }
        }


    }


    companion object {
        const val TAG = "ChangeTpinFragment"
        fun newInstance() = ChangeTpinFragment()
    }
}