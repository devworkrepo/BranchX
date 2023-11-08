package com.branchx.agent.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.branchx.agent.R
import com.branchx.agent.data.model.AepsTransactionResponse
import com.branchx.agent.databinding.ActivityAepsTransactionResultBinding
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.adapter.MiniStatementAdapter
import com.airbnb.lottie.LottieAnimationView

class AepsResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAepsTransactionResultBinding
    private lateinit var aepsTransactionResponse: AepsTransactionResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_aeps_transaction_result)

        aepsTransactionResponse = intent.getParcelableExtra(AppConstants.DATA)!!

        binding.tvTitle.text = when (aepsTransactionResponse.transactionType) {
            "CW" -> "Cash Withdrawal"
            "BE" -> "Balance Enquiry"
            else -> "Mini Statement"
        }

        binding.nestedScrollview.scrollTo(0, 0)

        binding.tvStatus.text = aepsTransactionResponse.message
        binding.tvTransactionAmount.text = aepsTransactionResponse.transactionAmount
        binding.tvTransactionType.text = aepsTransactionResponse.transactionType
        binding.tvTransactionId.text = aepsTransactionResponse.transactionId
        binding.tvTransactionTime.text = aepsTransactionResponse.transactionTime
        binding.tvAvailBalance.text = aepsTransactionResponse.availableAmount
        binding.tvMobile.text = aepsTransactionResponse.mobileNumber
        binding.tvAadharNumber.text = aepsTransactionResponse.aadhaarNumber

        if (aepsTransactionResponse.transactionType == "MS") {
            val adapter = MiniStatementAdapter().apply {
                this.addItems(ArrayList(aepsTransactionResponse.miniStatement))
                this.context = this@AepsResultActivity
            }
            if (adapter.itemCount > 0)
                binding.llStatement.show()
            binding.recyclerView.setup().adapter = adapter

        }

        val lottieAnimationView: LottieAnimationView = binding.lottieSuccess
        lottieAnimationView.set(LottieType.SUCCESS)
    }

    override fun onBackPressed() {
        if (aepsTransactionResponse.transactionType == "CW") gotoMainActivity()
        else super.onBackPressed()
    }
}
