package com.branchx.agent.ui.fragment.neo_banking

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.branchx.agent.R
import com.branchx.agent.base.BaseFragment
import com.branchx.agent.data.response.NeoBankingResponse
import com.branchx.agent.databinding.FragmentEnterDetailsBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.event.Events
import com.branchx.agent.ui.viewmodel.neo_banking.NeoBankingViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus


@AndroidEntryPoint
class EnterDetailsFragment : BaseFragment() {

    private lateinit var mBinding: FragmentEnterDetailsBinding
    private val neoViewModel: NeoBankingViewModel by viewModels()

    var progressDialog: Dialog? = null


    companion object {
        const val TAG = "EnterDetailsFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding = FragmentEnterDetailsBinding.bind(
            inflater.inflate(
                R.layout.fragment_enter_details,
                container,
                false
            )
        )
        bindViews()

        return mBinding.root
    }


    private fun bindViews() {
        val drawableStart =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_mobile, null)
        drawableStart?.setBounds(0, 0, 70, 70)
        mBinding.etMobileNumber.setCompoundDrawablesRelative(drawableStart, null, null, null)


        mBinding.mcvSendOtp.setOnClickListener {
            neoViewModel.getNeoOtpRequest(mBinding.etMobileNumber.text.toString())
        }

        setUpObserver()

    }

    private fun setUpObserver() {
        neoViewModel.getNeoOtp.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext(), "Loading...")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    Log.d(TAG, "NEO_OTP:: ${it.data.message}")
                    onSuccess(it.data)

                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }

            }

        }

    }


    private fun onSuccess(data: NeoBankingResponse) {
        when (data.status) {
            0 -> {
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
            }

            1 -> {
                Toast.makeText(requireActivity(), data.message, Toast.LENGTH_SHORT).show()
                EventBus.getDefault().postSticky(Events.MobileNumberEvent(mBinding.etMobileNumber.text.toString()))
                Log.d(TAG,"OTP_SENT::")
                findNavController().navigate(R.id.action_enterDetailsFragment_to_neoBankingOtpFragment)
            }

            else -> {
                data.message?.let { AppDialog.failure(requireActivity(), it) }
            }
        }

    }


}