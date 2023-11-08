package com.branchx.agent.ui.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.branchx.agent.ui.fragment.fund.FundRequestFragment
import com.branchx.agent.ui.fragment.fund.FundRequestReportFragment


class MoneyRequestTabFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FundRequestFragment.newInstance()
            1 -> fragment = FundRequestReportFragment.newInstance()
        }
        return fragment!!
    }

    override fun getCount()= 2


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "New Request"
            1 -> title = "Request Report"
        }
        return title
    }
}
