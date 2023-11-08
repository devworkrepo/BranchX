package com.branchx.agent.ui.activity

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.branchx.agent.databinding.ActivityComingsoonBinding
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.*
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.main.AepsServiceType
import com.branchx.agent.ui.viewmodel.AepsViewModel
import com.spayindia.app.util.PidParser2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComingSoonActivity : BaseActivity() {
    private lateinit var binding: ActivityComingsoonBinding
    private val viewModel: AepsViewModel by viewModels()

    private var spinnerDialog: SpinnerDialog? = null


    private lateinit var aepsServiceType: AepsServiceType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityComingsoonBinding.inflate(layoutInflater)


        setContentView(binding.root)

        aepsServiceType =
            intent.getSerializableExtra(AppConstants.AEPS_SERVICE_TYPE) as AepsServiceType
        viewModel.aepsServiceType = aepsServiceType

        val toolbarTitle = if (aepsServiceType == AepsServiceType.AEPS) "AEPS" else "Aadhaar Pay"

        setupToolbar(binding.toolbar.toolbar, toolbarTitle)

//        viewModel.fetchBankList()
//        fetchLocation(onFetch = {
//            viewModel.loacation = it
//        },showProgress = false)
    }

//    private fun fetchLocation(onFetch: (Location) -> Unit = {}, showProgress: Boolean = true) {
//
//        if(viewModel.loacation !=null){
//            onFetch(viewModel.loacation!!)
//        }
//        else{
//            PermissionUtil.locationPermission(this) {
//                if (!it) return@locationPermission
//                val isLocationServiceEnable = LocationService.isLocationEnabled(this)
//                if (isLocationServiceEnable) {
//
//                    if (showProgress) {
//                        progressDialog =
//                            AppDialog.progress(this, "Fetching location...")
//                    }
//
//                }
//            }
//        }
//    }

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

}