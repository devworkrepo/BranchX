package com.branchx.agent.helper.drawer

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class TitleMenu(title: String, items: List<SubTitle>, imageResource: Int) :
    ExpandableGroup<SubTitle>(title, items) {
    var imageResource = 0

    init {
        this.imageResource = imageResource
    }
}
