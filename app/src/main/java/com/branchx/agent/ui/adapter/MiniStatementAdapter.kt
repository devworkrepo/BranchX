package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.MiniStatement
import com.branchx.agent.databinding.ListMiniStatementBinding

class MiniStatementAdapter() : BaseRecyclerViewAdapter<MiniStatement, ListMiniStatementBinding>(R.layout.list_mini_statement) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListMiniStatementBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.miniStatement = item
    }
}