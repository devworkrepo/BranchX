package com.branchx.agent.helper.drawer


import android.view.View
import android.widget.TextView
import com.branchx.agent.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder


class SubTitleViewHolder(itemView: View) : ChildViewHolder(itemView) {
    private val subTitleTextView: TextView = itemView.findViewById(R.id.main_nav_submenu_item_title)

    fun setSubTitleName(name: String) {
        subTitleTextView.text = name

    }
}
