package com.branchx.agent.ui.dialog

import android.content.Context
import android.widget.*
import com.branchx.agent.R
import com.branchx.agent.helper.extns.setupAdapter
import com.branchx.agent.helper.util.AppConstants

object AepsDialogs {





    inline fun selectRDDevice(
        context: Context,
        selectedDevice: String,
        crossinline onApprove: (device: String, packageUrl: String) -> Unit
    ) = dialogConfiguration(context, R.layout.dialog_rd_device).apply {

        val mantraPackage = "com.mantra.rdservice"
        val morphoPackage = "com.scl.rdservice"
        val startekPackage = "com.acpl.registersdk"

        var selectRDPackage = mantraPackage
        val deviceList = arrayOf(
            AppConstants.MANTRA,
            AppConstants.MORPHO,
            AppConstants.STARTEK)

        var mSelectedDevice = selectedDevice

        val spinner = findViewById<Spinner>(R.id.spn_selectDevice)
        spinner.setupAdapter(deviceList, onItemSelected = {
            mSelectedDevice = it
            selectRDPackage = when (it) {
                AppConstants.MANTRA -> mantraPackage
                AppConstants.MORPHO -> morphoPackage
                AppConstants.STARTEK -> startekPackage
                else -> mantraPackage
            }
        })
        spinner.setSelection(deviceList.indexOf(selectedDevice))


        findViewById<TextView>(R.id.tv_message).text =
            "Please connect biometric device and complete process"
        findViewById<Button>(R.id.btn_proceed).setOnClickListener {
            onApprove(mSelectedDevice, selectRDPackage)
            dismiss()
        }
        show()
    }
}