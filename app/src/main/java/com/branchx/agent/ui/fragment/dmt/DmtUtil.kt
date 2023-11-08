package com.branchx.agent.ui.fragment.dmt

import com.branchx.agent.helper.enum.DmtType

object DmtUtil {
    fun title(dmtType: DmtType) = when(dmtType){
        DmtType.DMT_ONE -> "Dmt One"
        DmtType.DMT_TWO -> "Dmt Two"
    }
}