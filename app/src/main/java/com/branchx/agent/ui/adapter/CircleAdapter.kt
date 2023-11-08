package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.response.Circle
import com.branchx.agent.databinding.ListCircleBinding

class CircleAdapter() : BaseRecyclerViewAdapter<Circle, ListCircleBinding>(R.layout.list_circle) {
    override fun onBindViewHolder(holder: Companion.BaseViewHolder<ListCircleBinding>, position: Int) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

        binding.llMainLayout.setOnClickListener{onItemClick?.invoke(it,item,position)}
    }

    // onItemClick  : ((provider)->Unit)? = null
}