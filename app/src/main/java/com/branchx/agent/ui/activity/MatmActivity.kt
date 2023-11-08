package com.branchx.agent.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.data.response.MatmRequestResponse
import com.branchx.agent.databinding.ActivityMatmBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.requestForLocationPhoneReadState
import com.branchx.agent.helper.extns.show
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.LocationService
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.viewmodel.MatmViewModel
import com.fingpay.microatmsdk.HistoryScreen
import com.fingpay.microatmsdk.MicroAtmLoginScreen
import com.fingpay.microatmsdk.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatmActivity : BaseActivity() {

    private val viewModel: MatmViewModel by viewModels()
    private lateinit var binding: ActivityMatmBinding

    //dialogs
    private var initialProgress: Dialog? = null
    private var updateMatmRequestDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_matm)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        subscribeObservers()


    }

    private fun subscribeObservers() {
        viewModel.txnTypeResourceIdObs.observe(this, Observer {
            when (it) {
                R.id.rb_cw -> {
                    binding.llAmount.show()
                    viewModel.txnType = "CW"
                    viewModel.matmTxnType = Constants.CASH_WITHDRAWAL
                }
                R.id.rb_be -> {
                    binding.llAmount.hide()
                    viewModel.txnType = "BE"
                    viewModel.matmTxnType = Constants.BALANCE_ENQUIRY
                }
            }
        })

        viewModel.requestPermissionObs.observe(this) {
            if (it) {
                if (LocationService.locationEnabled(this)) {
                    requestForLocationPhoneReadState {
                        val mLocationManager =
                            getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        viewModel.deviceId = AppUtil.getDeviceId(this@MatmActivity)
                        viewModel.fetchLocation(mLocationManager)
                    }
                }
            }
        }

        viewModel.requestMatmObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    initialProgress = AppDialog.progress(this, "Initiating...")
                }
                is Resource.Success -> {
                    initialProgress?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.providerTxnId = it.data.txnId.orEmpty()
                        launchMatmActivity(it.data)
                    } else {
                        AppDialog.failure(this, it.data.message)
                    }
                }
                is Resource.Failure -> {
                    initialProgress?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.updateMatmRequestObs.observe(this) {
            when (it) {
                is Resource.Loading ->
                    updateMatmRequestDialog = AppDialog.progress(this, "Please wait...")

                is Resource.Success -> {
                    updateMatmRequestDialog?.dismiss()
                    AppDialog.matmResponse(
                        context = this,
                        status = viewModel.matmResStatus,
                        message = viewModel.matmResMessage,
                        transactionAmount = viewModel.matmResTransAmount,
                        balanceAmount = viewModel.matmResBalAmount,
                        bankRnn = viewModel.matmResBankRRN,
                        bankName = viewModel.matmResBankName,
                        cardNumber = viewModel.matmResCardNumber,
                        transactionType = if (viewModel.txnType == "CW") "Cash Withdrawal" else "Balance enquiry"
                    ).apply {

                    }
                }
                is Resource.Failure -> {
                    updateMatmRequestDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.historyScreenLaunchObs.observe(this) {
            val intent = Intent(this, HistoryScreen::class.java)
            intent.putExtras(it)
            startActivity(intent)
        }
    }

    private fun launchMatmActivity(data: MatmRequestResponse) {

        val dataBundle = bundleOf(
            Constants.MERCHANT_USERID to data.loginId.orEmpty(),
            Constants.MERCHANT_PASSWORD to data.loginPin.orEmpty(),
            Constants.AMOUNT to viewModel.amount,
            Constants.REMARKS to if (viewModel.txnType == "BE") "Balance Enquiry" else "Cash Withdrawal",
            Constants.MOBILE_NUMBER to viewModel.customerMobileNumber,
            Constants.AMOUNT_EDITABLE to false,
            Constants.TXN_ID to "tr"+data.txnId.orEmpty(),
            Constants.SUPER_MERCHANTID to data.superMerchantId.orEmpty(),
            Constants.IMEI to viewModel.deviceId,
            Constants.LATITUDE to viewModel.latitude.toDouble(),
            Constants.LONGITUDE to viewModel.longitude.toDouble(),
            Constants.TYPE to viewModel.matmTxnType
        )


        AppUtil.logger(dataBundle.toString())
        val mIntent = Intent(this@MatmActivity, MicroAtmLoginScreen::class.java).apply {
            this.putExtras(dataBundle)
        }

        startActivityForResult(mIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    updateMatmRequest(it)
                } ?: run {
                    AppDialog.pending(this, "Pending")
                }
            } else
                AppDialog.pending(this, "Pending")
        }
    }


    private fun updateMatmRequest(intent: Intent) {

        viewModel.matmTxnType

        val status = intent.getBooleanExtra(Constants.TRANS_STATUS, false)
        val transAmount = intent.getDoubleExtra(Constants.TRANS_AMOUNT, 0.0)
        val balAmount = intent.getDoubleExtra(Constants.BALANCE_AMOUNT, 0.0)
        var bankRrn = intent.getStringExtra(Constants.RRN)
        var bankName = intent.getStringExtra(Constants.BANK_NAME)
        var cardNumber = intent.getStringExtra(Constants.CARD_NUM)
        var message = intent.getStringExtra(Constants.MESSAGE)

        if (message == null) message = ""
        if (bankRrn == null) bankRrn = ""
        if (cardNumber == null) cardNumber = ""
        if (bankName == null) bankName = ""


        viewModel.let {
            it.matmResStatus = status
            it.matmResBankName = bankName
            it.matmResBankRRN = bankRrn
            it.matmResMessage = message
            it.matmResCardNumber = cardNumber
            it.matmResTransAmount = transAmount.toString()
            it.matmResBalAmount = balAmount.toString()

             it.updateMatmRequest()

        }
    }
}