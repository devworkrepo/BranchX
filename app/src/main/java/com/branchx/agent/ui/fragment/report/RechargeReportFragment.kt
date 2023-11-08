package com.branchx.agent.ui.fragment.report

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RechargeReportFragment :
    BaseFragment<FragmentReportBinding>(R.layout.fragment_report){

    private var key: Int = 3

    companion object {
        fun newInstance(key: Int) = RechargeReportFragment().apply {
            this.arguments = bundleOf("key" to key)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getInt("key") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutNoDataFound.root.hide()
        binding.fabFilter.hide()
        binding.layoutComingSoon.root.show()

    }



}