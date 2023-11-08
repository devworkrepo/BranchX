package com.branchx.agent.ui.activity

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.branchx.agent.data.model.*
import com.branchx.agent.databinding.ActivityEkycBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.*
import com.branchx.agent.ui.dialog.AepsDialogs
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.viewmodel.EKycViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EkycActivity : BaseActivity() {

    private lateinit var binding: ActivityEkycBinding
    private val viewModel: EKycViewModel by viewModels()
    private var bankSpinnerDialog: SpinnerDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEkycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar.toolbar, "E-Kyc")


        viewModel.aepsList =
            intent.getParcelableArrayListExtra<AepsBank>(AepsActivity.BANK_LIST)
                    as List<AepsBank>

        viewModel.useInfo = intent.getParcelableExtra(AepsActivity.USER_INFO)!!

        viewModel.latitude = intent.getStringExtra(AepsActivity.LATITUDE)!!
        viewModel.longitude = intent.getStringExtra(AepsActivity.LONGIUDE)!!


        setupBankList()
        setupStep()

        binding.edBankName.setOnClickListener {
            bankSpinnerDialog?.showSpinerDialog()
        }

        binding.btnSendOtp.setOnClickListener {
            if (!validateDeviceSerialNumber()) return@setOnClickListener
            viewModel.sendKycOtp()
        }

        binding.btnProceed.setOnClickListener {
            if (viewModel.stepType == EKycViewModel.StepType.STEP_ONE) {
                if (!validateOtpVerify()) return@setOnClickListener
                viewModel.verifyOtp()
            } else {
                if (!validateProceedEKyc()) return@setOnClickListener
                showRdServiceDialog()
            }
        }


        ViewUtil.maskAadhaarNumber(binding.edAadharNumber)

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilBankName,
            binding.tilAadharNumber,
            binding.tilOtp,
            binding.tilDeviceSerialNumber,
        )

        subscribeObservers()

        viewModel.onboard()
    }
    private fun showRdServiceDialog() {
        val pidOption =
            "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"2\" iCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"15000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\" posh=\"UNKNOWN\" /></PidOptions>"
        AepsDialogs.selectRDDevice(this, appPreference.selectRdDevice) { device, packageUrl ->
            appPreference.selectRdDevice = device
            val intent = Intent()
            intent.setPackage(packageUrl)
            intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
            intent.putExtra("PID_OPTIONS", pidOption)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val result = data.getStringExtra("PID_DATA")
                if (result != null) {
                    try {
                        val respString = PidParser.parse(result)
                        if (respString[0].equals("0", ignoreCase = true)) {
                            viewModel.pidData = result
                            viewModel.proceedEkyc()
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


    private fun subscribeObservers() {
        viewModel.sendObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.otpType = EKycViewModel.OTPType.RESEND
                        binding.btnSendOtp.text = "Resend OTP"
                        makeToast(it.data.message)
                    } else AppDialog.failure(this, it.data.message.toString())
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        })

        viewModel.verifyOtpObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(this)
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()

                    when (it.data.status) {
                        1 -> {
                            AppDialog.success(this, it.data.message.toString()).apply {
                                setOnDismissListener {
                                    viewModel.stepType = EKycViewModel.StepType.STEP_TWO
                                    setupStep()
                                }
                            }
                        }
                        else -> AppDialog.failure(this, it.data.message.toString())
                    }
                }

                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        })

        viewModel.authObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(this)
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    when (it.data.status) {
                        1 -> {
                            AppDialog.success(this, it.data.message).apply {
                                setOnDismissListener {
                                    gotoMainActivity()
                                }
                            }
                        }
                        else -> {
                            AppDialog.failure(this, it.data.message)
                        }
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        })

        viewModel.onBoardingObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(this, "Initiating OnBoarding")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if(it.data.status !=1){
                        AppDialog.failure(this, it.data.message).apply {
                            setOnDismissListener { onBackPressed() }
                        }
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        })
    }

    private fun setupBankList() {


        val bankNames = viewModel.aepsList.map { it.name }

        bankSpinnerDialog =
            SpinnerDialog(
                this,
                bankNames as ArrayList<String>,
                "Search Bank"
            )

        bankSpinnerDialog?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(false)
            spinner.bindOnSpinerListener { item, _ ->

                val bank = viewModel.aepsList.find { it.name == item }
                viewModel.bankName = bank!!.name
                viewModel.bankId = bank.id
                binding.edBankName.setText(bank.name)
            }
        }
    }

    private fun setupStep() {
        if (viewModel.stepType == EKycViewModel.StepType.STEP_ONE) {
            binding.cvStep1.show()
            binding.cvStep2.hide()
            binding.btnProceed.text = "Proceed"
        } else {

            binding.cvStep1.hide()
            binding.cvStep2.show()
            binding.btnProceed.text = "Capture & Proceed"
        }
    }

    private fun validateDeviceSerialNumber(): Boolean {
        var isValid = true
        viewModel.deviceSerialNumber = binding.edDeviceSerialNumber.text.toString()

        if (viewModel.deviceSerialNumber.length < 5) {
            isValid = false
            binding.tilDeviceSerialNumber.apply {
                error = "Enter minimum 5 characters"
                isErrorEnabled = true
            }
        } else binding.tilDeviceSerialNumber.isErrorEnabled = false
        return isValid
    }


    private fun validateProceedEKyc(): Boolean {
        var isValid = true
        viewModel.aadhaarNumber = binding.edAadharNumber.text.toString()

        if (viewModel.bankName.isEmpty()) {
            isValid = false
            binding.tilBankName.apply {
                error = "Select a bank"
                isErrorEnabled = true
            }
        } else binding.tilBankName.isErrorEnabled = false

        if (viewModel.aadhaarNumber.length != 12) {
            isValid = false
            binding.tilAadharNumber.apply {
                error = "Enter 12 digits aadhaar number"
                isErrorEnabled = true
            }
        } else binding.tilAadharNumber.isErrorEnabled = false
        viewModel.aadhaarNumber.replace("-", "")
        return isValid
    }

    private fun validateOtpVerify(): Boolean {

        var isValid = true
        viewModel.otp = binding.edOtp.text.toString()
        viewModel.deviceSerialNumber = binding.edDeviceSerialNumber.text.toString()


        if (viewModel.deviceSerialNumber.length < 5) {
            isValid = false
            binding.tilDeviceSerialNumber.apply {
                error = "Enter minimum 5 characters"
                isErrorEnabled = true
            }
        } else binding.tilDeviceSerialNumber.isErrorEnabled = false

        if (viewModel.otp.length != 7) {
            isValid = false
            binding.tilOtp.apply {
                error = "Enter 7 digits OTP"
                isErrorEnabled = true
            }
        } else binding.tilOtp.isErrorEnabled = false

        return isValid
    }

}