package com.branchx.agent.helper.drawer

import android.content.Context
import com.branchx.agent.R
import com.branchx.agent.helper.extns.bindColor
import java.util.*

class NavData {
    companion object {
        fun getMenuNavigation(context: Context): ArrayList<NavMenuModel> {
            val menu = ArrayList<NavMenuModel>()

            return retailerMenu(menu)
        }

        private fun retailerMenu(menu: ArrayList<NavMenuModel>): ArrayList<NavMenuModel> {

            menu.add(NavMenuModel(MainMenu.HOME, R.drawable.home))
            menu.add(NavMenuModel(MainMenu.PASSWORD, R.drawable.password))
            menu.add(NavMenuModel(MainMenu.WALLET_REQUEST, R.drawable.store))
            //menu.add(NavMenuModel(MainMenu.SUPPORTS, R.drawable.ic_baseline_support_agent_24))
            menu.add(NavMenuModel(MainMenu.TPIN, R.drawable.ic_tpin_new))
            menu.add(NavMenuModel(MainMenu.LOGOUT, R.drawable.power))
            return menu
        }

    }

    object MainMenu {
        const val HOME: String = "Home"
        const val PASSWORD: String = "Change Password"
        const val TPIN: String = "Change TPin"
        const val WALLET_REQUEST: String = "Wallet Request"
        const val SUPPORTS: String = "Support"
        const val LOGOUT: String = "Logout"
    }
}