package com.branchx.agent.ui.adapter.report

import com.branchx.agent.R
import com.branchx.agent.data.model.DmtReport
import com.branchx.agent.databinding.ListDmtReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.adapter.BaseRecyclerViewAdapter

class DmtReportAdapter() : BaseRecyclerViewAdapter<DmtReport, ListDmtReportBinding>(R.layout.list_dmt_report) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListDmtReportBinding>,
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


    private fun showHideContentInfo(item: DmtReport, binding: ListDmtReportBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }
    
    private fun setupStatus(item : DmtReport, binding: ListDmtReportBinding){
        when(item.statusId){
            "1" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "2" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.yellow)
        }
    }
}