package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.support.SupportContact
import com.branchx.agent.databinding.ListSupportCardBinding

class SupportContactAdapter() :
    BaseRecyclerViewAdapter<SupportContact, ListSupportCardBinding>(R.layout.list_support_card) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListSupportCardBinding>,
        position: Int
    ) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

    }
}