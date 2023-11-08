package com.branchx.agent.ui.fragment.report

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.branchx.agent.R
import com.branchx.agent.data.model.report.AepsReport
import com.branchx.agent.data.model.report.AepsReportResponse
import com.branchx.agent.databinding.FragmentReportBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setup
import com.branchx.agent.helper.extns.show
import com.branchx.agent.helper.util.DateUtil
import com.branchx.agent.ui.adapter.report.AepsReportAdapter
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.dialog.FilterDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AepsReportFragment :
    BaseFragment<FragmentReportBinding>(R.layout.fragment_report),
    SwipeRefreshLayout.OnRefreshListener {

    lateinit var viewModel: ReportViewModel
    private var key: Int = 0

    companion object {
        fun newInstance(key: Int) = AepsReportFragment().apply {
            this.arguments = bundleOf("key" to key)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getInt("key") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(key.toString(), ReportViewModel::class.java)


        binding.refreshLayout.setOnRefreshListener(this)

        subscribeObservers()

        binding.fabFilter.setOnClickListener {
            FilterDialog.commonDateFilter(
                requireActivity(),
                onSearchClick = {fromDate, toDate ->
                    viewModel.fromDate = fromDate
                    viewModel.toDate = toDate
                    viewModel.fetchAepsReport()
                }
            )
        }

        setupDate()

        viewModel.fetchAepsReport()
    }

    private fun setupDate(){
        val date = DateUtil.currentDate()
        viewModel.fromDate = DateUtil.getDateBeforeOneWeek()
        viewModel.toDate = date
    }
    private fun subscribeObservers() {
        viewModel.run {
            viewModel.fetchAepsDmtReportObs.observe(viewLifecycleOwner) {
                onAepsReportResponse(it)
            }
        }
    }

    private fun onAepsReportResponse(it: Resource<AepsReportResponse>) {
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
                    refreshLayout.isRefreshing = false
                }

                when (it.data.status) {
                    1 -> {
                        binding.recyclerView.show()
                        binding.layoutNoDataFound.rootLayout.hide()
                        setupReportList(it.data.report)
                    }
                    10 -> {
                        binding.recyclerView.hide()
                        binding.layoutNoDataFound.rootLayout.show()
                    }
                    else -> AppDialog.failure(requireActivity(),it.data.message.toString())
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

    private fun setupReportList(data: List<AepsReport>) {
        val adapter = AepsReportAdapter().apply {
            addItems(ArrayList(data))
            context = requireActivity()
        }
        binding.recyclerView.setup().adapter = adapter
    }

    override fun onRefresh() {
        viewModel.fetchAepsReport()
    }

}