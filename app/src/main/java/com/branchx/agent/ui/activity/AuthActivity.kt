package com.branchx.agent.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.branchx.agent.R
import com.branchx.agent.helper.extns.makeToast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class AuthActivity : BaseActivity(), InstallStateUpdatedListener {

    private val updateRequestCode = 102

    private var appUpdateManager: AppUpdateManager? = null
    private var isForceUpdate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        checkForInAppUpdate()
    }


    override fun onResume() {
        super.onResume()
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackbar(
                    getString(R.string.app_update_msg),
                    getString(R.string.restart),
                    onClickListener
                )
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }
    }


    private fun checkForInAppUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    requestUpdate(appUpdateInfo)
                    appUpdateManager?.registerListener(this@AuthActivity)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
            }
        }


    }

    private fun requestUpdate(
        appUpdateInfo: AppUpdateInfo?,
        updateType: Int = AppUpdateType.IMMEDIATE
    ) {
        appUpdateManager?.startUpdateFlowForResult(
            appUpdateInfo!!,
            updateType,
            this@AuthActivity,
            updateRequestCode
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.updateRequestCode && resultCode != AppCompatActivity.RESULT_OK) {

            if (isForceUpdate) {
                checkForInAppUpdate()
                makeToast("Update require!")
            }
        }
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showSnackbar(
                getString(R.string.app_update_msg),
                getString(R.string.restart),
                onClickListener
            )
            appUpdateManager?.unregisterListener(this)
        }
    }

    private fun showSnackbar(
        message: String,
        actionText: String,
        clickListener: View.OnClickListener
    ) {
        findViewById<View>(R.id.parentLayout)?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionText, clickListener).show()
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        appUpdateManager?.completeUpdate()
    }
}