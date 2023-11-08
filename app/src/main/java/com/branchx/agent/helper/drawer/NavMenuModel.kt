package com.branchx.agent.helper.drawer

import java.util.ArrayList

class NavMenuModel {
    var menuTitle: String
    var menuIconDrawable: Int = 0
    var subMenu: MutableList<SubMenuModel>

    constructor(menuTitle: String, menuIconDrawable: Int) {
        this.menuTitle = menuTitle
        this.menuIconDrawable = menuIconDrawable
        this.subMenu = ArrayList()
    }

    constructor(menuTitle: String, menuIconDrawable: Int, subMenu: ArrayList<SubMenuModel>) {
        this.menuTitle = menuTitle
        this.menuIconDrawable = menuIconDrawable
        this.subMenu = ArrayList()
        this.subMenu.addAll(subMenu)
    }

    class SubMenuModel(var subMenuTitle: String)
}