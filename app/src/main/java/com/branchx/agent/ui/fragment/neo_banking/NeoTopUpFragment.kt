package com.branchx.agent.ui.fragment.neo_banking

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.base.BaseFragment
import com.branchx.agent.data.response.NeoBankingResponse
import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse
import com.branchx.agent.databinding.FragmentNeoTopUpBinding
import com.branchx.agent.databinding.TopUpPinBottomSheetLayoutBinding
import com.branchx.agent.databinding.ViewBalanceOtpLayoutBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.event.Events
import com.branchx.agent.ui.viewmodel.neo_banking.NeoBankingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NeoTopUpFragment : BaseFragment() {

    private lateinit var mBinding: FragmentNeoTopUpBinding

    private val neoViewModel: NeoBankingViewModel by viewModels()

    private var mobileNumber: String? = null
    private var verifyOtpResponse: NeoBankingVerifyOtpResponse? = null
    private var progressDialog: Dialog? = null


    companion object {
        const val TAG = "NeoTopUpFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        mBinding = FragmentNeoTopUpBinding.bind(
            inflater.inflate(
                R.layout.fragment_neo_top_up, container, false
            )
        )
        bindViews()
        return mBinding.root
    }

    private fun bindViews() {
        mBinding.tv500.setOnClickListener {
            mBinding.etTopUp.setText("500")
        }
        mBinding.tv1000.setOnClickListener {
            mBinding.etTopUp.setText("1000")
        }
        mBinding.tv2000.setOnClickListener {
            mBinding.etTopUp.setText("2000")
        }
        mBinding.tv5000.setOnClickListener {
            mBinding.etTopUp.setText("5000")
        }


        mBinding.mcvProceed.setOnClickListener {
            if (mBinding.etTopUp.text.toString().isNotEmpty() && mBinding.etTopUp.text.toString()
                    .toInt() >= 10
            ) {
                showTpinBottomSheetDialog()
            } else {
                Toast.makeText(requireActivity(), "Enter valid amount", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.mcvBalance.setOnClickListener {
            mobileNumber?.let {
                neoViewModel.getViewBalanceOtp(it)
                setUpViewBalanceObserver()
            }

        }

    }


    private fun setUpTopUpObserver(bottomSheetDialog: BottomSheetDialog) {
        neoViewModel.getTopUp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    bottomSheetDialog.dismiss()
                    onSuccess(it.data, isTopUp = true)
                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    requireActivity().handleNetworkFailure(it.exception)
                }

            }
        }
    }

    private fun setUpViewBalanceObserver() {
        neoViewModel.getViewBalanceOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    Log.d(TAG, "VIEW_BALANCE_OTP:: ${it.data.message}")
                    onSuccess(it.data, isTopUp = false)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    requireActivity().handleNetworkFailure(it.exception)
                }

            }
        }
    }

    private fun setUpViewBalanceResponseObserver(bottomSheetBinding: BottomSheetDialog) {
        neoViewModel.getVerifyViewBalanceOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    onSuccess(it.data, bottomSheetBinding)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    requireActivity().handleNetworkFailure(it.exception)
                }

            }
        }
    }


    private fun onSuccess(data: NeoBankingResponse, isTopUp: Boolean) {

        Log.d(TAG, "NeoBankingResponse_Top_Up:: $data")

        val bundle = bundleOf(
            "neoBankingResponse" to data,
            "amount" to mBinding.etTopUp.text.toString()
        )
        when (data.status) {
                0 -> {
                if (isTopUp) {
                    findNavController().navigate(
                        R.id.action_neoTopUpFragment_to_neoTransferResponseFragment,
                        bundle
                    )
                } else {
                    Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                }
            }

            1 -> {
                if (isTopUp) {
                    Log.d(TAG, "TOP_UP_RESPONSE:: $data")
                    findNavController().navigate(
                        R.id.action_neoTopUpFragment_to_neoTransferResponseFragment,
                        bundle
                    )
                } else {
                    showOtpBottomSheetDialog()
                    Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                }
            }

            3 -> {
                findNavController().navigate(
                    R.id.action_neoTopUpFragment_to_neoTransferResponseFragment,
                    bundle
                )
            }

            else -> {
                findNavController().navigate(
                    R.id.action_neoTopUpFragment_to_neoTransferResponseFragment,
                    bundle
                )
                data.message?.let { AppDialog.failure(requireActivity(), it) }
            }
        }

    }

    private fun onSuccess(
        data: NeoBankingVerifyOtpResponse,
        bottomSheetBinding: BottomSheetDialog,
    ) {
        Log.d(TAG, "VIEW_BALANCE:: $data")

        when (data.status) {
            0 -> {
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
            }

            1 -> {
                mBinding.tvWalletBalance.text = "Wallet Balance \n₹${data.userDetails?.balance}"
                bottomSheetBinding.dismiss()
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
            }

            else -> {
                data.message?.let { AppDialog.failure(requireActivity(), it) }
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
                            mobileNumber?.let { mobileNumber ->
                                neoViewModel.verifyViewBalanceOtp(mobileNumber, otp)
                                setUpViewBalanceResponseObserver(this)
                            }

                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Enter valid otp",
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

    private fun showTpinBottomSheetDialog() {
        val bottomSheetBinding =
            TopUpPinBottomSheetLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        BottomSheetDialog(requireActivity(), R.style.MyBottomSheetDialogTheme).apply {
            if (!this.isShowing) {
                this.setCancelable(false)
                this.setContentView(bottomSheetBinding.root)
                this.show()

                var tPin = ""
                bottomSheetBinding.let { sheet ->
                    Log.d(TAG, "TPIN:: $tPin")
                    sheet.mcvProceed.setOnClickListener {
                        tPin = sheet.pinview.text.toString()
                        if (tPin.isNotEmpty() && tPin.length == 4) {
                            mobileNumber?.let { mobileNumber ->
                                neoViewModel.topUpWallet(
                                    tPin,
                                    mobileNumber,
                                    mBinding.etTopUp.text.toString(),
                                    verifyOtpResponse?.userDetails?.customerId!!
                                )
                                setUpTopUpObserver(this)
                            }

                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Enter valid Tpin",
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


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMobileNumberEvent(event: Events.MobileNumberEvent) {
        event.mobileNumber?.let {
            mobileNumber = it
            Log.i(TAG, "FragmentLs: Mobile Number")
        }

    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun neoBankingVerifyOtpResponseEvent(event: Events.NeoBankingVerifyOtpResponseEvent) {
        event.neoBankingVerifyOtpResponse?.let {
            verifyOtpResponse = it
            Log.i(TAG, "FragmentLs: $it")
        }

    }


}