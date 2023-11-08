package com.branchx.agent.ui.activity

import android.os.Bundle
import com.branchx.agent.databinding.ActivityBillPayRechargeBinding
import com.branchx.agent.helper.util.AppConstants
import com.branchx.agent.ui.fragment.main.ServiceType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RechargeBillPayActivity : BaseActivity() {

    private lateinit var binding : ActivityBillPayRechargeBinding
    var serviceType : ServiceType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPayRechargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        serviceType = intent.getSerializableExtra(AppConstants.SERVICE_TYPE_KEY) as ServiceType


    }
}