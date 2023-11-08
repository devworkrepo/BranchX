package com.branchx.agent.ui.activity

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.helper.extns.launchActivity
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.AutoLogoutUtil
import com.branchx.agent.helper.util.RootUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity () : AppCompatActivity(){

    @Inject
    lateinit var appPreference: AppPreference
    @Inject
    lateinit var autoLogoutUtil : AutoLogoutUtil

    var progressDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }


    override fun onResume() {
        super.onResume()
        if (RootUtil.isDeviceRooted)
            launchActivity(ErrorActivity::class.java, bundleOf(
                ErrorActivity.TYPE to ErrorActivity.ROOT,
                ErrorActivity.TITLE to resources.getString(R.string.rooted_device_title),
                ErrorActivity.DESCRIPTION to resources.getString(R.string.rooted_device_message)
            ),true)
       else{
            autoLogoutUtil.cancelTimer()
            if (autoLogoutUtil.isSessionTimeout) {
                autoLogoutUtil.logout()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (autoLogoutUtil.isAppInBackground(this)) {
            autoLogoutUtil.startUserSession()
        }

        AppUtil.logger("onstop called")
    }


}