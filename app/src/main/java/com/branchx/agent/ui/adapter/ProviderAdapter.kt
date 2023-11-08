package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.Provider
import com.branchx.agent.databinding.ListProviderBinding

class ProviderAdapter() : BaseRecyclerViewAdapter<Provider, ListProviderBinding>(R.layout.list_provider) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListProviderBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.provider = item

        binding.llMainLayout.setOnClickListener{onItemClick?.invoke(it,item,position)}
    }

    // onItemClick  : ((provider)->Unit)? = null
}