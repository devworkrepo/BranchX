package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.FundRequestReport
import com.branchx.agent.databinding.ListFundRequestBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show

class FundRequestReportAdapter() :
    BaseRecyclerViewAdapter<FundRequestReport, ListFundRequestBinding>(R.layout.list_fund_request) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListFundRequestBinding>,
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

        setupStatus(item, binding)
    }


    private fun showHideContentInfo(item: FundRequestReport, binding: ListFundRequestBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }

    private fun setupStatus(item: FundRequestReport, binding: ListFundRequestBinding) {
        when (item.statusId) {
            "1" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "2" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.yellow)
        }
    }
}