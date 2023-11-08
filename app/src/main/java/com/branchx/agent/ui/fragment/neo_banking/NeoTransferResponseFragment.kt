package com.branchx.agent.ui.fragment.neo_banking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.base.BaseFragment
import com.branchx.agent.data.response.NeoBankingResponse
import com.branchx.agent.data.response.NeoBankingVerifyOtpResponse
import com.branchx.agent.databinding.FragmentNeoTransferResponseBinding
import com.branchx.agent.helper.extns.LottieType
import com.branchx.agent.helper.extns.backPressHandler
import com.branchx.agent.helper.extns.gotoMainActivity
import com.branchx.agent.helper.extns.set
import com.branchx.agent.helper.extns.setupBGColor
import com.branchx.agent.helper.extns.text
import com.branchx.agent.ui.event.Events
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NeoTransferResponseFragment : BaseFragment() {

    private lateinit var mBinding: FragmentNeoTransferResponseBinding
    private val args: NeoTransferResponseFragmentArgs by navArgs()

    private var neoBankingResponse: NeoBankingResponse? = null

    private var verifyOtpResponse: NeoBankingVerifyOtpResponse? = null


    companion object {
        const val TAG = "NeoTransferFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding = FragmentNeoTransferResponseBinding.inflate(inflater, container, false)
        neoBankingResponse = args.neoBankingResponse
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()

    }


    private fun bindViews() {

        backPressHandler {
            gotoMainActivity()
        }


        mBinding.apply {
            neoBankingResponse?.let {
                Toast.makeText(requireActivity(), "${it.message}", Toast.LENGTH_SHORT).show()
                when (it.status) {
                    0, 2 -> {
                        val color = ContextCompat.getColor(requireContext(), R.color.red)
                        activity?.window?.statusBarColor = color
                        lottieTransactionStatus.set(LottieType.FAILED)
                        tvStatus.text = activity?.text(R.string.failure)
                        tvStatus.setupBGColor(R.color.red)
                        llHome.setupBGColor(R.color.red)
                        it.message?.let { message ->
                            tvStatusLabel.text = message
                            tvStatusLabel.setTextColor(color)
                        }
                    }

                    1 -> {
                        val color = ContextCompat.getColor(requireContext(), R.color.green)
                        activity?.window?.statusBarColor = color
                        lottieTransactionStatus.set(LottieType.SUCCESS)
                        tvStatus.text = activity?.text(R.string.success)
                        tvStatus.setupBGColor(R.color.green)
                        llHome.setupBGColor(R.color.green)
                        it.message?.let { message ->
                            tvStatusLabel.text = message
                            tvStatusLabel.setTextColor(color)
                        }
                    }

                    3 -> {
                        val color = ContextCompat.getColor(requireContext(), R.color.yellow)
                        activity?.window?.statusBarColor = color
                        lottieTransactionStatus.set(LottieType.PENDING)
                        tvStatus.text = activity?.text(R.string.pending)
                        tvStatus.setupBGColor(R.color.yellow)
                        llHome.setupBGColor(R.color.yellow)
                        it.message?.let { message ->
                            tvStatusLabel.text = message
                            tvStatusLabel.setTextColor(color)
                        }
                    }

                    else -> {
                        val color = ContextCompat.getColor(requireContext(), R.color.red)
                        activity?.window?.statusBarColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        lottieTransactionStatus.set(LottieType.FAILED)
                        tvStatus.text = activity?.text(R.string.failure)
                        tvStatus.setupBGColor(R.color.red)
                        llHome.setupBGColor(R.color.red)
                        it.message?.let { message ->
                            tvStatusLabel.text = message
                            tvStatusLabel.setTextColor(color)
                        }
                    }
                }
            }


            verifyOtpResponse?.let {
                llAccountNumber.isVisible = false
                llBankName.isVisible = false
                tvBeneficiaryName.text = "${it.userDetails?.firstName} ${it.userDetails?.lastName}"
                tvAmount.text = "₹ ${args.amount}"
            }

            llHome.setOnClickListener { requireActivity().gotoMainActivity() }

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
            Log.d(TAG, "FragmentLs: $it")
        }

    }
}