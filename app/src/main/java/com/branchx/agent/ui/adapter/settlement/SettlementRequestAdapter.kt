package com.branchx.agent.ui.adapter.settlement

import com.branchx.agent.R
import com.branchx.agent.data.model.settlement.SettlementRequest
import com.branchx.agent.databinding.ListSettlementRequestBinding
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.adapter.BaseRecyclerViewAdapter

class SettlementRequestAdapter() : BaseRecyclerViewAdapter<SettlementRequest, ListSettlementRequestBinding>(R.layout.list_settlement_request) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListSettlementRequestBinding>,
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


    private fun showHideContentInfo(item: SettlementRequest, binding: ListSettlementRequestBinding) {
        if (item.isContentInfoVisible) {
            binding.llContentInfo.show()
        } else {
            binding.llContentInfo.hide()
        }
    }
    
    private fun setupStatus(item : SettlementRequest, binding: ListSettlementRequestBinding){
        when(item.status){
            "SUCCESS" -> binding.tvStatus.setupTextColor(R.color.green_color)
            "REJECTED","REJECT" -> binding.tvStatus.setupTextColor(R.color.red)
            else -> binding.tvStatus.setupTextColor(R.color.yellow)
        }
    }
}