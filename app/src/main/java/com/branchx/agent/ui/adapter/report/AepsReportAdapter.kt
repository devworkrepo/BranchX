package com.branchx.agent.ui.adapter.report

import com.branchx.agent.R
import com.branchx.agent.data.model.report.AepsReport
import com.branchx.agent.databinding.ListAepsReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.adapter.BaseRecyclerViewAdapter

class AepsReportAdapter() : BaseRecyclerViewAdapter<AepsReport, ListAepsReportBinding>(R.layout.list_aeps_report) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListAepsReportBinding>,
        position: Int
    ) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

        showHideContentInfo(item, binding)
        binding.cardView.setOnClickListener {
            item.isContentInfoVisible = !item.isContentInfoVisible
            showHideContentInfo(item, binding)
        }

        setupStatus(item,binding)
    }


    private fun showHideContentInfo(item: AepsReport, binding: ListAepsReportBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }
    
    private fun setupStatus(item : AepsReport, binding: ListAepsReportBinding){
        when(item.statusId){
            "1" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "2" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.yellow)
        }
    }
}