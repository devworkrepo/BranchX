package com.branchx.agent.helper.drawer

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.branchx.agent.R
import com.bumptech.glide.Glide
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class TitleViewHolder(itemView: View, private var adapter: NavMenuAdapter) :
    GroupViewHolder(itemView) {
    private val titleView: TextView = itemView.findViewById(R.id.nav_menu_item_title)
    private var titleString: String? = null
    private val arrow: ImageView = itemView.findViewById(R.id.nav_menu_item_arrow)
    private val icon: ImageView = itemView.findViewById(R.id.nav_menu_item_icon)

    fun setGenreTitle(context: Context, title: ExpandableGroup<*>) {
        if (title is TitleMenu) {
            if (title.imageResource != 0) {

                Glide.with(context).load(title.imageResource)
                    .into(icon)
                title.getTitle()
            }

            titleString = title.getTitle()
            titleView.text = title.getTitle()
            titleView.setTextColor(ContextCompat.getColor(context,R.color.black))

            if (title.items.size > 0) {
                arrow.visibility = View.VISIBLE

                var isExpand = false
                for (i in adapter.isExpandList.indices) {
                    if (titleString == adapter.isExpandList[i]) {
                        isExpand = true
                        break
                    }
                }
                if (isExpand) {
                    arrow.setImageResource(R.drawable.v_arrow_up)
                } else {
                    arrow.setImageResource(R.drawable.v_arrow_down)
                }
            } else {
                arrow.visibility = View.GONE
            }
        }
    }

    override fun expand() {
        titleString?.let { adapter.isExpandList.add(it) }
        adapter.notifyDataSetChanged()
    }

    override fun collapse() {
        adapter.isExpandList.remove(titleString)
        adapter.notifyDataSetChanged()
    }
}
