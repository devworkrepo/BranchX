package com.branchx.agent.ui.adapter.viewpager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.branchx.agent.ui.fragment.LedgerReportFragment
import com.branchx.agent.ui.fragment.report.AepsReportFragment
import com.branchx.agent.ui.fragment.report.DmtReportFragment
import com.branchx.agent.ui.fragment.report.MatmReportFragment
import com.branchx.agent.ui.fragment.report.RechargeReportFragment
import com.branchx.agent.ui.fragment.settlement.SettlementNewFragment
import com.branchx.agent.ui.fragment.settlement.SettlementReportFragment
import com.branchx.agent.ui.fragment.settlement.SettlementRequestFragment


class TransactionTabViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DmtReportFragment.newInstance(0)
            1 -> fragment = AepsReportFragment.newInstance(1)
            2 -> fragment = MatmReportFragment.newInstance(2)
            3 -> fragment = RechargeReportFragment.newInstance(3)
        }
        return fragment!!
    }

    override fun getCount()= 4


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "DMT"
            1 -> title = "AEPS"
            2 -> title = "MATM"
            3 -> title = "RECHARGE"
        }
        return title
    }
}



class SettlementTabViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SettlementNewFragment.newInstance(0)
            1 -> fragment = SettlementRequestFragment.newInstance(1)

        }
        return fragment!!
    }

    override fun getCount()= 2


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "Make Settlement"
            1 -> title = "All Request"
        }
        return title
    }
}





class LedgerTabViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SettlementReportFragment.newInstance(1)
            1 -> fragment = LedgerReportFragment.newInstance(0)
            2-> fragment = LedgerReportFragment.newInstance(1)
        }
        return fragment!!
    }

    override fun getCount()= 3


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "WALLET"
            1-> title = "AEPS"
            2 -> title = "MATM"
        }
        return title
    }
}
