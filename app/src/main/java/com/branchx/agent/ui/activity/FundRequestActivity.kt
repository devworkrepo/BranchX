package com.branchx.agent.ui.activity

import android.os.Bundle
import com.branchx.agent.databinding.ActivityCommonTabViewBinding
import com.branchx.agent.helper.extns.setupToolbar
import com.branchx.agent.helper.util.AndroidBug5497Workaround
import com.branchx.agent.ui.adapter.MoneyRequestTabFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FundRequestActivity : BaseActivity() {

    private lateinit var binding : ActivityCommonTabViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommonTabViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidBug5497Workaround.assistActivity(this);

        setupToolbar(binding.toolbar.toolbar,"Money Request")
        renderViewPager()
    }


    private fun renderViewPager() {
        val viewPagerAdapter = MoneyRequestTabFragmentAdapter(supportFragmentManager)
        binding.viewpager.adapter = viewPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewpager)
    }

}