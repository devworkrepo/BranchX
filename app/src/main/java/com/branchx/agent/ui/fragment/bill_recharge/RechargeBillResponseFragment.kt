package com.branchx.agent.ui.fragment.bill_recharge

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.branchx.agent.R
import com.branchx.agent.data.response.BillPaymentResponse
import com.branchx.agent.databinding.BillRechargeResponseFragmentBinding
import com.branchx.agent.helper.extns.LottieType
import com.branchx.agent.helper.extns.gotoMainActivity
import com.branchx.agent.helper.extns.set
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.activity.RechargeBillPayActivity
import com.branchx.agent.ui.fragment.BaseFragment


class RechargeBillResponseFragment :
    BaseFragment<BillRechargeResponseFragmentBinding>(R.layout.bill_recharge_response_fragment) {

    private  var origin: String ? =null
    private var billPaymentResponse: BillPaymentResponse? = null
    private var mobileNumber : String ? =null
    private var caNumber : String ? = null
    private var providerName : String ? = null
    private var amount : String ? = null

    private var activityInstance: RechargeBillPayActivity? = null


    private var backPressCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            origin = it.getString(AppConstants.ORIGIN_KEY)
            billPaymentResponse = it.getParcelable(AppConstants.RESPONSE_KEY)
            mobileNumber = it.getString(AppConstants.MOBILE_NUMBER_KEY)
            amount = it.getString(AppConstants.AMOUNT_KEY)
            providerName = it.getString(AppConstants.PROVIDER_NAME_KEY)
            caNumber = it.getString(AppConstants.CA_NUMBER_KEY)
        }



        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if(backPressCount ==2){
                    isEnabled = false
                    //activity?.onBackPressed()
                    activity?.gotoMainActivity()
                }
                else {
                    backPressCount +=1
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityInstance = (activity as RechargeBillPayActivity)
        when (origin) {
            BillPayFragment.ORIGIN -> {
                setupInit(billPaymentResponse?.status)
                setupViewForBillPayment()
            }
            RechargeFragment.ORIGIN -> {
                //todo(recharge response view implementation)
            }
        }

        binding.llHome.setOnClickListener{ activity?.gotoMainActivity() }
    }

    private fun setupViewForBillPayment() {

            binding.let {
                it.tvMobileNumber.text = mobileNumber
                it.tvOperator.text = providerName
                it.tvAmount.text = amount
                it.tvCaBillNumber.text = caNumber
                //from response
                it.tvMessage.text = billPaymentResponse?.message
                it.tvTxnTime.text = billPaymentResponse?.date
                it.tvTransactionId.text = billPaymentResponse?.transactionId
                it.tvOperatorRefNo.text = billPaymentResponse?.operatorId
        }
    }



    private fun setupInit(status: Int?) {

        val color: Int
        val contentBackgroundColor : Int
        val strStatus: String
        val lottieType: LottieType
        when (status) {
            1 -> {
                strStatus = "Success"
                color = ContextCompat.getColor(requireContext(), R.color.green)
                contentBackgroundColor = ContextCompat.getColor(requireContext(), R.color.green2)
                lottieType = LottieType.SUCCESS
            }
            2 -> {
                strStatus = "Failed"
                color = ContextCompat.getColor(requireContext(), R.color.red)
                contentBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red2)
                lottieType = LottieType.FAILED
            }
            else -> {
                strStatus = "Pending"
                color = ContextCompat.getColor(requireContext(), R.color.yellow)
                contentBackgroundColor = ContextCompat.getColor(requireContext(), R.color.yellow2)
                lottieType = LottieType.PENDING
            }
        }



        binding.tvStatus.let {
            it.text = strStatus
            it.setBackgroundColor(color)
        }
        binding.lottieTransactionStatus.set(lottieType)
        binding.llHome.setBackgroundColor(color)
        binding.contentLayout.setBackgroundColor(contentBackgroundColor)
    }
}