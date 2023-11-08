package com.branchx.agent.helper.drawer

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.branchx.agent.R
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import java.util.*

class NavMenuAdapter(
    private val context: Context,
    groups: List<ExpandableGroup<*>>,
    activity: Activity
) : ExpandableRecyclerViewAdapter<TitleViewHolder, SubTitleViewHolder>(groups) {
    private val mListener: MenuItemClickListener
    var selectedItemParent = ""
    var selectedItemChild = ""
    var isExpandList = ArrayList<String>()

    init {
        this.mListener = activity as MenuItemClickListener
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nav_menu_item, parent, false)

        val holder = TitleViewHolder(view, this)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): SubTitleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nav_submenu_item, parent, false)
        val holder = SubTitleViewHolder(view)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindChildViewHolder(
        holder: SubTitleViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>, childIndex: Int
    ) {
        val menu = group as TitleMenu
        val subTitle = menu.items[childIndex]
        subTitle.name?.let { holder.setSubTitleName(it) }
        holder.itemView.setOnClickListener { view ->
            selectedItemParent = menu.title
            selectedItemChild = subTitle.name.toString()
            subTitle.name?.let { mListener.onMenuItemClick(it) }
            notifyDataSetChanged()
        }
    }

    override fun onBindGroupViewHolder(
        holder: TitleViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        val menu = group as TitleMenu
        holder.setGenreTitle(context, menu)
        if (menu.items.size < 1) {
            holder.itemView.setOnClickListener { view ->
                selectedItemParent = menu.title
                selectedItemChild = ""
                mListener.onMenuItemClick(menu.title)
                notifyDataSetChanged()
            }
        }
    }

    interface MenuItemClickListener {
        fun onMenuItemClick(item: String)
    }
}
