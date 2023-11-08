package com.branchx.agent.ui.activity

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.data.model.*
import com.branchx.agent.databinding.ActivityAepsBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.*
import com.branchx.agent.ui.dialog.AepsDialogs
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.dialog.ConfirmDialog
import com.branchx.agent.ui.fragment.main.AepsServiceType
import com.branchx.agent.ui.viewmodel.AepsViewModel
import com.spayindia.app.util.PidParser2
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AepsActivity : BaseActivity() {

    private var doF2FAuth = false

    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    private lateinit var mResponseData: AepsBankUserResponse
    private lateinit var binding: ActivityAepsBinding
    private val viewModel: AepsViewModel by viewModels()

    private var spinnerDialog: SpinnerDialog? = null


    private lateinit var aepsServiceType: AepsServiceType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAepsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aepsServiceType =
            intent.getSerializableExtra(AppConstants.AEPS_SERVICE_TYPE) as AepsServiceType
        viewModel.aepsServiceType = aepsServiceType

        if (aepsServiceType == AepsServiceType.AEPS) {
            binding.radioGroup.show()
        } else binding.radioGroup.hide()

        val toolbarTitle = if (aepsServiceType == AepsServiceType.AEPS) "AEPS" else "Aadhaar Pay"

        setupToolbar(binding.toolbar.toolbar, toolbarTitle)

        setupTransactionTypeRadioGroup()

        binding.btnProceed.setOnClickListener {


            if (doF2FAuth) {
                if (viewModel.bankName.isEmpty()) {
                    binding.tilBankName.apply {
                       error = "Select Bank"
                        isErrorEnabled = true
                    }
                    return@setOnClickListener
                } else binding.tilBankName.isErrorEnabled = false
            }
            else {
                if (!viewModel.validateInput(binding)) return@setOnClickListener
            }
            fetchLocation(onFetch = {
                showRDDeviceDialog()
            })

        }

        viewModel.fetchBankList()

        binding.edBankName.setOnClickListener { spinnerDialog?.showSpinerDialog() }

        ViewUtil.maskAadhaarNumber(binding.edAadharNumber)


        subscribeObservers()

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilBankName,
            binding.tilAadharNumber,
            binding.tilCustomerMobile,
            binding.tilAmount
        )

        binding.tvKycMessage.setOnClickListener {

            fetchLocation(onFetch = {
                Intent(this, EkycActivity::class.java).apply {
                    this.putExtras(
                        bundleOf(
                            BANK_LIST to mResponseData.aepsBankListResponse.aepsBank,
                            USER_INFO to mResponseData.userInfo,
                            LATITUDE to it.latitude.toString(),
                            LONGIUDE to it.longitude.toString()
                        )
                    )
                    startActivity(this)
                }
            })
        }

        fetchLocation(onFetch = {
            viewModel.loacation = it
        },showProgress = false)
    }

    private fun fetchLocation(onFetch: (Location) -> Unit = {}, showProgress: Boolean = true) {

        if(viewModel.loacation !=null){
            onFetch(viewModel.loacation!!)
        }
        else{
            PermissionUtil.locationPermission(this) {
                if (!it) return@locationPermission
                val isLocationServiceEnable = LocationService.isLocationEnabled(this)
                if (isLocationServiceEnable) {

                    if (showProgress) {
                        progressDialog =
                            AppDialog.progress(this, "Fetching location...")
                    }

                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            progressDialog?.dismiss()
                            if(viewModel.loacation == null){
                                onFetch(location)
                            }
                        }
                    })
                }
            }
        }
    }


    private fun subscribeObservers() {
        viewModel.aepsBankUserObs.observe(this, Observer {
            onBankListResponse(it)
        })

        viewModel.transactionObs.observe(this, Observer {
            onTransactionResponse(it)
        })

        viewModel.f2fAuthObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.fetchBankList()
                    } else AppDialog.failure(this, it.data.message)
                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        })
    }

    private fun onTransactionResponse(it: Resource<AepsTransactionResponse>) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = AppDialog.transactionProgress(this)
            }
            is Resource.Success -> {
                progressDialog?.dismiss()


                if (it.data.status == 1) {
                    it.data.aadhaarNumber = viewModel.aadhaarNumber
                    it.data.mobileNumber = viewModel.customerMobileNumber
                    it.data.transactionType = viewModel.getTransactionTypeData()

                    launchActivity(
                        AepsResultActivity::class.java,
                        bundleOf(AppConstants.DATA to it.data)
                    )

                } else AppDialog.failure(this, it.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                handleNetworkFailure(it.exception)
            }
        }
    }

    private fun onBankListResponse(it: Resource<AepsBankUserResponse>) {

        when (it) {
            is Resource.Loading -> {
                binding.svContent.hide()
                binding.progress.root.show()
            }
            is Resource.Success -> {
                binding.svContent.show()
                binding.progress.root.hide()

                if (it.data.aepsBankListResponse.status == 1 ||
                    it.data.aepsBankListResponse.status == 404) {
                    doF2FAuth = it.data.aepsBankListResponse.status == 404
                    mResponseData = it.data
                    setupAepsBankList(it.data.aepsBankListResponse.aepsBank!!)
                    setup2FAuthUI()
                } else AppDialog.failure(this, it.data.aepsBankListResponse.message)

                if (it.data.userInfo.status.equals("pending", true)) {

                    binding.tvKycMessage.show()
                }
            }
            is Resource.Failure -> {
                binding.svContent.show()
                binding.progress.root.hide()
                handleNetworkFailure(it.exception)
            }
        }
    }

    private fun setup2FAuthUI() {
        binding.run {
            if (doF2FAuth) {
                this.ll2fAuth.show()
                this.cvAmount.hide()
                this.tilAadharNumber.hide()
                this.tilCustomerMobile.hide()
            } else {
                this.ll2fAuth.hide()
                this.cvAmount.show()
                this.tilAadharNumber.show()
                this.tilCustomerMobile.show()
            }
        }
    }

    private fun setupAepsBankList(bankList: List<AepsBank>) {

        val bankNames = bankList.map { it.name }

        spinnerDialog =
            SpinnerDialog(
                this,
                bankNames as ArrayList<String>,
                "Search Bank"
            )

        spinnerDialog?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(false)
            spinner.bindOnSpinerListener { item, position ->
                viewModel.bankName = item
                viewModel.bankCode = bankList.find { it.name == item }?.id.toString()
                binding.edBankName.setText(item)
            }
        }
    }


    private fun setupTransactionTypeRadioGroup() {
        binding.radioGroup.check(binding.rbCashWithdrawal.id)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbCashWithdrawal.id -> {
                    viewModel.transactionType = AepsViewModel.TransactionType.CASH_WITHDRAWAL
                    binding.tilAmount.show()
                }
                binding.rbBalanceEnquiry.id -> {
                    viewModel.transactionType = AepsViewModel.TransactionType.BALANCE_ENQUIRY
                    binding.tilAmount.hide()
                }
                binding.rbMiniStatement.id -> {
                    viewModel.transactionType = AepsViewModel.TransactionType.MINI_STATEMENT
                    binding.tilAmount.hide()
                }
                else -> {
                    viewModel.transactionType = AepsViewModel.TransactionType.CASH_WITHDRAWAL
                    binding.tilAmount.show()
                }
            }
        }
    }


    private fun showRDDeviceDialog() {

        AepsDialogs.selectRDDevice(
            context = this,
            selectedDevice = appPreference.selectRdDevice,
            onApprove = { device, packageUrl ->
                appPreference.selectRdDevice = device
                viewModel.deviceName = device

                captureData(packageUrl)
            })
    }

    private fun captureData(packageUrl: String) {
        try {
            val pidOption = """<PidOptions ver="1.0">
       <Opts env="P" fCount="1" fType="2" format="0" iCount="0" iType="0" pCount="0" pType="0" pidVer="2.0" posh="UNKNOWN" timeout="10000"/>
    </PidOptions>"""
            val intent = Intent()
            intent.setPackage(packageUrl)
            intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
            intent.putExtra("PID_OPTIONS", pidOption)
            startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE)

        } catch (e: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            viewModel.fetchBankList()
        } else if (resultCode == 11) {
            viewModel.fetchBankList()
        } else {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val result = data.getStringExtra("PID_DATA")
                    if (result != null) {
                        try {
                            val respString = PidParser.parse(result)
                            if (respString[0].equals("0", ignoreCase = true)) {
                                viewModel.pidData = result
                                val serialNumber = PidParser2.getDeviceSerialNumber(result)
                                viewModel.deviceSerialNumber = serialNumber

                                if (doF2FAuth) viewModel.proceedF2FAuth()
                                else showConfirmAepsTransactionDialog()

                            } else makeToast(respString[1])
                        } catch (e: java.lang.Exception) {
                            AppDialog.failure(
                                this,
                                "Captured failed, please check biometric device is connected!"
                            )
                        }
                    } else AppDialog.failure(
                        this,
                        "Captured failed, please check biometric device is connected!"
                    )

                } else AppDialog.failure(
                    this,
                    "Captured failed, please check biometric device is connected!"
                )
            } else AppDialog.failure(
                this,
                "Captured failed, please check biometric device is connected!"
            )

        }

    }

    private fun showConfirmAepsTransactionDialog() {

        ConfirmDialog.aepsTransactionConfirmation(
            context = this,
            aadhaarNumber = viewModel.aadhaarNumber,
            transactionType = viewModel.getTransactionTypeDataFullForm(),
            amount = viewModel.amount,
            bankName = viewModel.bankName
        ).apply {
            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dismiss()
                viewModel.aepsTransaction()
            }
        }
    }

    private fun makeFieldEmptyValue() {
        binding.edCustomerMobile.setText("")
        binding.edAmount.setText("")
        binding.edAmount.setText("")
        binding.edBankName.setText("")
        binding.edAadharNumber.setText("")

        viewModel.customerMobileNumber = AppConstants.EMPTY
        viewModel.amount = AppConstants.EMPTY
        viewModel.aadhaarNumber = AppConstants.EMPTY
        viewModel.bankName = AppConstants.EMPTY
        viewModel.bankCode = AppConstants.EMPTY
    }

    companion object {
        const val USER_INFO = "user_info"
        const val BANK_LIST = "bank_list"
        const val LATITUDE = "latitude"
        const val LONGIUDE = "longitude"
        private const val RD_SERVICE_RESPONSE_DODE = 10
    }
}