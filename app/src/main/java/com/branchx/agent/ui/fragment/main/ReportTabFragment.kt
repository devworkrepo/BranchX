package com.branchx.agent.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.branchx.agent.R
import com.branchx.agent.ui.adapter.viewpager.LedgerTabViewPagerAdapter
import com.branchx.agent.ui.adapter.viewpager.SettlementTabViewPagerAdapter
import com.branchx.agent.ui.adapter.viewpager.TransactionTabViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionReportTabFragment : Fragment() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabs: TabLayout

    private lateinit var tabType: BottomNavType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabType = arguments?.getSerializable("type") as BottomNavType

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewpager = view.findViewById(R.id.viewpager)
        tabs = view.findViewById(R.id.tabs)
        renderViewPager()
    }

    companion object {
        fun newInstance(type: BottomNavType) = TransactionReportTabFragment().apply {
            arguments = bundleOf("type" to type)
        }
    }


    private fun renderViewPager() {
        val viewPagerAdapter = when (tabType) {
            BottomNavType.REPORT -> {
                TransactionTabViewPagerAdapter(childFragmentManager)
            }
            BottomNavType.STATEMENT -> {
                LedgerTabViewPagerAdapter(childFragmentManager)
            }
            BottomNavType.SETTLEMENT -> {
                SettlementTabViewPagerAdapter(childFragmentManager)
            }
        }
        viewpager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewpager)
    }

}

enum class BottomNavType {
    REPORT, SETTLEMENT, STATEMENT
}