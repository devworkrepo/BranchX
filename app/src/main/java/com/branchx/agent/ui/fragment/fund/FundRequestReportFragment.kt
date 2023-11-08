package com.branchx.agent.ui.fragment.fund

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.branchx.agent.R
import com.branchx.agent.data.model.FundRequestReport
import com.branchx.agent.data.model.FundRequestReportResponse
import com.branchx.agent.databinding.FragmentReport2Binding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setup
import com.branchx.agent.helper.extns.show
import com.branchx.agent.helper.util.DateUtil
import com.branchx.agent.ui.adapter.FundRequestReportAdapter
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.dialog.FilterDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FundRequestReportFragment :
    BaseFragment<FragmentReport2Binding>(R.layout.fragment_report2),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ReportViewModel by viewModels()


    companion object {
        fun newInstance()=FundRequestReportFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshLayout.setOnRefreshListener(this)

        subscribeObservers()

        binding.fabFilter.setOnClickListener {
            FilterDialog.commonDateFilter(
                requireActivity(),
                onSearchClick = {fromDate, toDate ->
                    viewModel.fromDate = fromDate
                    viewModel.toDate = toDate
                    viewModel.fetchFundRequestReports()
                }
            )

        }

        setupDate()

        viewModel.fetchFundRequestReports()
    }

    private fun setupDate(){
        val date = DateUtil.currentDate()
        viewModel.fromDate = DateUtil.getDateBeforeOneWeek()
        viewModel.toDate = date
    }
    private fun subscribeObservers() {
        viewModel.run {
            viewModel.fundRequestReportObs.observe(viewLifecycleOwner) {
                onReportsResponse(it)
            }
        }
    }

    private fun onReportsResponse(it: Resource<FundRequestReportResponse>) {
        when (it) {
            is Resource.Loading -> {
                binding.run {
                    progress.root.show()
                    recyclerView.hide()
                    binding.layoutNoDataFound.root.hide()
                }
            }
            is Resource.Success -> {
                binding.run {
                    progress.root.hide()
                    recyclerView.show()
                    refreshLayout.isRefreshing = false
                }

                when (it.data.status) {
                    1 -> {
                        setupReportList(it.data.reports)
                    }
                    10 -> {
                        binding.layoutNoDataFound.root.show()
                    }
                    else -> AppDialog.failure(requireActivity(),"Failed")
                }
            }
            is Resource.Failure -> {
                binding.run {
                    progress.root.hide()
                    recyclerView.show()
                }
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun setupReportList(data: List<FundRequestReport>) {
        val adapter = FundRequestReportAdapter().apply {
            addItems(ArrayList(data))
            context = requireActivity()
        }
        binding.recyclerView.setup().adapter = adapter
    }

    override fun onRefresh() {
        viewModel.fetchFundRequestReports()
    }

}