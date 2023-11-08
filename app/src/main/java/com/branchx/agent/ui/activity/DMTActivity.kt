package com.branchx.agent.ui.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.branchx.agent.DmtNavGraphArgs
import com.branchx.agent.R
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.util.AppConstants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DMTActivity : BaseActivity() {

    private var navHostFragment : NavHostFragment? = null
    private var navController : NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmt)

         val dmtType : DmtType = DmtType.DMT_TWO

        navHostFragment  = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment?.navController

        navController?.setGraph(R.navigation.dmt_nav_graph)
        navController?.setGraph(navController.graph, DmtNavGraphArgs(dmtType!!).toBundle())



    }
}