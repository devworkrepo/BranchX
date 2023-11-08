package com.branchx.agent.ui.adapter.settlement

import com.branchx.agent.R
import com.branchx.agent.data.model.settlement.SettlementReport
import com.branchx.agent.databinding.ListSettlementReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.adapter.BaseRecyclerViewAdapter

class SettlementReportAdapter() : BaseRecyclerViewAdapter<SettlementReport, ListSettlementReportBinding>(R.layout.list_settlement_report) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListSettlementReportBinding>,
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


    private fun showHideContentInfo(item: SettlementReport, binding: ListSettlementReportBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }
    
    private fun setupStatus(item : SettlementReport, binding: ListSettlementReportBinding){
        when(item.debitOrCredit){
            "Credit" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "Debit" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.purple_700)
        }
    }
}