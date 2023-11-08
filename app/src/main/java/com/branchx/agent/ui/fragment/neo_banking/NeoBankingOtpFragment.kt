package com.branchx.agent.ui.fragment.neo_banking

import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.base.BaseFragment
import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse
import com.branchx.agent.databinding.FragmentNeoBankingOtpBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.event.Events
import com.branchx.agent.ui.viewmodel.neo_banking.NeoBankingViewModel
import com.branchx.agent.utils.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NeoBankingOtpFragment : BaseFragment() {

    private lateinit var mBinding: FragmentNeoBankingOtpBinding

    private val neoViewModel: NeoBankingViewModel by viewModels()

    private lateinit var inputMethodManager: InputMethodManager

    private var progressDialog: Dialog? = null

    private var mobileNumber: String? = null
    private var otp: String? = null

    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null


    companion object {
        const val TAG = "NeoBankingOtpFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentNeoBankingOtpBinding.bind(
            inflater.inflate(
                R.layout.fragment_neo_banking_otp,
                container,
                false
            )
        )

        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setUpOtpInputs()
//        autoOTPReceiver()
        bindViews()
        return mBinding.root
    }


    private fun bindViews() {

        mBinding.txtMobile.text = mobileNumber

        mBinding.btnVerify.setOnClickListener {
            if (mBinding.pinview.text.toString().length != 6) {
                Toast.makeText(requireActivity(), "Enter valid code", Toast.LENGTH_SHORT).show()
            } else {
                verifyOtp()
            }
        }


    }

    private fun verifyOtp() {
        otp = mBinding.pinview.text.toString()
        otp?.let {
            neoViewModel.verifyNeoOtp(mobileNumber!!, it)
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            setUpObserver()
        }
    }


    private fun setUpObserver() {
        neoViewModel.verifyNeoOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    Log.d(TAG, "VERIFY_OTP:: ${it.data.message}")
                    Log.d(TAG, "VERIFY_OTP_RESPONSE:: ${it.data}")
                    onSuccess(it.data)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }

            }

        }
    }


    private fun onSuccess(data: NeoBankingVerifyOtpResponse) {
        when (data.status) {
            0 -> {
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
            }

            1 -> {
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_neoBankingOtpFragment_to_neoTopUpFragment)

                EventBus.getDefault().postSticky(Events.NeoBankingVerifyOtpResponseEvent(data))
            }

            else -> {
                data.message?.let { AppDialog.failure(requireActivity(), it) }
            }
        }

    }


    private fun autoOTPReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        requireActivity().registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        )

        smsBroadcastReceiver!!.setUpOtpListener(object :
            SmsBroadcastReceiver.SmsBroadcastReceiverListener {

            override fun onOTPSuccess(otp: String) {
                val o1 = Character.getNumericValue(otp[0])
                val o2 = Character.getNumericValue(otp[1])
                val o3 = Character.getNumericValue(otp[2])
                val o4 = Character.getNumericValue(otp[3])
                val o5 = Character.getNumericValue(otp[4])
                val o6 = Character.getNumericValue(otp[5])

                mBinding.pinview.setText("$o1$o2$o3$o4$o5$o6")
            }

            override fun onOTPTimeOut() {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setUpOtpInputs() {
        mBinding.pinview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                otp = mBinding.pinview.text.toString()
                otp?.let {
                    if (it.length == 6) {
                        verifyOtp()
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMobileNumberEvent(event: Events.MobileNumberEvent) {
        event.mobileNumber?.let {
            mobileNumber = it
            Log.i(TAG, "FragmentLs: Mobile Number $it")
        }

    }


    override fun onStop() {
        super.onStop()
//        requireActivity().unregisterReceiver(smsBroadcastReceiver)
        EventBus.getDefault().unregister(this)
    }
}