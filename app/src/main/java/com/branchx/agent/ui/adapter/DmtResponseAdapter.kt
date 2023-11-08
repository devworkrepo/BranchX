package com.branchx.agent.ui.adapter

import com.branchx.agent.R
import com.branchx.agent.data.model.DmtTransaction
import com.branchx.agent.databinding.ListMoneyTransactionResponseBinding


class DmtResponseAdapter :
    BaseRecyclerViewAdapter<DmtTransaction, ListMoneyTransactionResponseBinding>(R.layout.list_money_transaction_response) {

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListMoneyTransactionResponseBinding>,
        position: Int
    ) {
        holder.binding.moneyTransaction = items[position]
    }
}
