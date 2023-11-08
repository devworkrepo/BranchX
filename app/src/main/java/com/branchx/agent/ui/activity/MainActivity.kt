package com.branchx.agent.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.branchx.agent.R
import com.branchx.agent.databinding.ActivityMainBinding
import com.branchx.agent.helper.drawer.NavData
import com.branchx.agent.helper.drawer.NavMenuAdapter
import com.branchx.agent.helper.drawer.NavUtil
import com.branchx.agent.helper.extns.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.branchx.agent.ui.fragment.main.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity() : BaseActivity(), NavMenuAdapter.MenuItemClickListener,
    InstallStateUpdatedListener {

    var isHomeFragment: Boolean = true
    private var appUpdateManager: AppUpdateManager? = null
    private var isForceUpdate = true
    private val updateRequestCode = 102

    private lateinit var binding: ActivityMainBinding
    private var bottomNavigationView: BottomNavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupToolbar(binding.toolbar.toolbar, "branchx Agent", false)

        setupBottomNavigationBar(savedInstanceState)

//        binding.tvMobileNumber.text = appPreference.mobile
//        binding.tvUserName.text = appPreference.user.name

       // binding.ivUserProfile.setupNetworkImage(appPreference.user.profilePic)

        NavUtil.setNavigationDrawerMenu(this)
        checkForInAppUpdate()


    }

    private fun checkForInAppUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    requestUpdate(appUpdateInfo)
                    appUpdateManager?.registerListener(this@MainActivity)
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
            this@MainActivity,
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


    override fun onResume() {
        super.onResume()

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
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



    private fun setupBottomNavigationBar(savedInstanceState: Bundle?) {
        bottomNavigationView = findViewById(binding.bottomNavigationView.id)
        bottomNavigationView?.setOnNavigationItemSelectedListener {

            bottomNavigationView?.menu?.setGroupCheckable(0, true, true);

            val selectedFragment = when (it.itemId) {
                R.id.menu_dashboard -> {
                    isHomeFragment = true
                    binding.toolbar.toolbar.title = "branchX Agent"
                    HomeFragment.newInstance()
                }
                R.id.menu_reports -> {
                    isHomeFragment = false
                    binding.toolbar.toolbar.title = "Report"
                    TransactionReportTabFragment.newInstance(BottomNavType.REPORT)
                }

                R.id.menu_ledger -> {
                    isHomeFragment = false
                    binding.toolbar.toolbar.title = "Statement"
                    TransactionReportTabFragment.newInstance(BottomNavType.STATEMENT)
                }


                R.id.menu_settlement -> {
                    isHomeFragment = false
                    binding.toolbar.toolbar.title = "Settlement"
                    TransactionReportTabFragment.newInstance(BottomNavType.SETTLEMENT)
                }
                else -> {
                    isHomeFragment = true
                    binding.toolbar.toolbar.title = "branchX Agent"
                    HomeFragment.newInstance()
                }
            }
            setFragment(selectedFragment)

            return@setOnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null) {
            setFragment(HomeFragment.newInstance())
            binding.toolbar.toolbar.title = "branchX Agent"
        }

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }




    private fun setFragment(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onMenuItemClick(item: String) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        if (item != NavData.MainMenu.LOGOUT) {
            binding.toolbar.toolbar.title = item
            bottomNavigationView?.menu?.setGroupCheckable(0, false, true);
            bottomNavigationView?.hide()
            isHomeFragment = false
        }

        when (item) {
            NavData.MainMenu.HOME -> {
                isHomeFragment = true
                bottomNavigationView?.show()
                bottomNavigationView?.selectedItemId = R.id.menu_dashboard
            }
            NavData.MainMenu.PASSWORD -> {
                setFragment(ChangePasswordFragment.newInstance())
            }

            NavData.MainMenu.WALLET_REQUEST -> {
                setFragment(R2RTransferFragment.newInstance())
            }

            NavData.MainMenu.TPIN -> {
                setFragment(ChangeTpinFragment.newInstance())
            }

//            NavData.MainMenu.SUPPORTS -> {
//                setFragment(SupportContactFragment.newInstance())
//            }


            NavData.MainMenu.LOGOUT -> logout()
        }
    }

    override fun onBackPressed() {

        if (isHomeFragment)
            super.onBackPressed()
        else {
            bottomNavigationView?.show()
            bottomNavigationView?.selectedItemId = R.id.menu_dashboard;
        }
    }

    private fun logout() {
        appPreference.clearDataOnLogout()
        gotoLoginActivity()
    }






}

