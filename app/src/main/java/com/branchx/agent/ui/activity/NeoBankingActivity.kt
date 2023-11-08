package com.branchx.agent.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.branchx.agent.R
import com.branchx.agent.databinding.ActivityNeoBankingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeoBankingActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityNeoBankingBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNeoBankingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpNavHost()

    }

    private fun setUpNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.enterDetailsFragment -> {
                    mBinding.toolbar.customToolbar.isVisible = true
                    mBinding.toolbar.tvTitle.text = "Enter Details"
                    mBinding.toolbar.customToolbar.apply {
                        setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                        setOnClickListener {
                            finish()
                        }
                    }
                }

                R.id.neoBankingOtpFragment -> {
                    mBinding.toolbar.customToolbar.isVisible = true
                    mBinding.toolbar.tvTitle.text = "Verify OTP"
                    mBinding.toolbar.customToolbar.apply {
                        setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                        setOnClickListener {
                            navController.popBackStack()
                        }
                    }

                }

                R.id.neoTopUpFragment -> {
                    mBinding.toolbar.customToolbar.isVisible = true
                    mBinding.toolbar.tvTitle.text = "Top Up Amount"
                    mBinding.toolbar.customToolbar.apply {
                        setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                        setOnClickListener {
                            finish()
                        }
                    }

                }

                R.id.neoTransferResponseFragment -> {
                    mBinding.toolbar.customToolbar.isVisible = false
                }

            }
        }


    }
}