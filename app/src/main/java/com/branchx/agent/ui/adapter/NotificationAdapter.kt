package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.databinding.ListNotificationBinding

class NotificationAdapter() :
    BaseRecyclerViewAdapter<com.branchx.agent.data.model.notification.Notification, ListNotificationBinding>(R.layout.list_notification) {
    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListNotificationBinding>,
        position: Int
    ) {
        val binding = holder.binding
        val item = items[position]
        binding.item = item

        binding.tvCount.text = (position+1).toString() +"."

    }
}