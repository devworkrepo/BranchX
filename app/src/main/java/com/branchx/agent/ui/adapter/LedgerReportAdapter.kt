package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.LedgerReport
import com.branchx.agent.databinding.ListLedgerReportBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.show

class LedgerReportAdapter(private val key : Int) : BaseRecyclerViewAdapter<LedgerReport, ListLedgerReportBinding>(R.layout.list_ledger_report) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListLedgerReportBinding>,
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

        if(key == 0){
            binding.ivLogo.setImageResource(R.drawable.ic_launcher_aeps)
        }
        else {
            binding.ivLogo.setImageResource(R.drawable.atm)
        }

    }


    private fun showHideContentInfo(item: LedgerReport, binding: ListLedgerReportBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }

}