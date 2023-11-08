package com.branchx.agent.helper.drawer

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.branchx.agent.R
import com.branchx.agent.helper.extns.bindColor


object NavUtil {


    fun setNavigationDrawerMenu(activity: Activity) {
        val menuList = getMenuList(activity)
        val adapter = NavMenuAdapter(activity, menuList, activity)
        val navMenuDrawer = activity.findViewById<View>(R.id.main_nav_menu_recyclerview) as RecyclerView
        navMenuDrawer.adapter = adapter
        navMenuDrawer.layoutManager = LinearLayoutManager(activity)
        navMenuDrawer.adapter = adapter
    }

    private fun getMenuList(activity: Activity): List<TitleMenu> {
        val list: MutableList<TitleMenu> = ArrayList<TitleMenu>()
        val menu: ArrayList<NavMenuModel> = NavData.getMenuNavigation(activity)
        for (i in menu.indices) {
            val subMenu: ArrayList<SubTitle> = ArrayList<SubTitle>()
            if (menu[i].subMenu.size > 0) {
                for (j in 0 until menu[i].subMenu.size) {
                    subMenu.add(SubTitle(menu.get(i).subMenu.get(j).subMenuTitle))
                }
            }
            list.add(TitleMenu(menu.get(i).menuTitle, subMenu, menu.get(i).menuIconDrawable))
        }
        return list
    }
}