package com.branchx.agent.ui.adapter.report

import com.branchx.agent.R
import com.branchx.agent.data.model.report.MatmReport
import com.branchx.agent.databinding.ListMatmReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.adapter.BaseRecyclerViewAdapter

class MatmReportAdapter() : BaseRecyclerViewAdapter<MatmReport, ListMatmReportBinding>(R.layout.list_matm_report) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListMatmReportBinding>,
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


    private fun showHideContentInfo(item: MatmReport, binding: ListMatmReportBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }
    
    private fun setupStatus(item : MatmReport, binding: ListMatmReportBinding){
        when(item.statusId){
            "1" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "2" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.yellow)
        }
    }
}