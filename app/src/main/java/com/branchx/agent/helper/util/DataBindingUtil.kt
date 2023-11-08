@file:JvmName("BindingUtil")

package com.branchx.agent.helper.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.branchx.agent.R
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.setupTextColor
import com.branchx.agent.helper.extns.show

@BindingAdapter("setVisibilityFromLiveData")
fun setVisibilityFromLiveData(textView: TextView, data: LiveData<String>) {
    try {
        textView.text = data.value
        if (data.value == "") {
            textView.hide()
            textView.text = ""
        } else {
            textView.show()
        }
    } catch (e: Exception) {
        textView.hide()
    }
}

@BindingAdapter("setTransactionStatus")
fun setStatusFromInt(view : View,data : Int){
    if(view is TextView){
        when (data) {
            1 -> view.apply {
                text = "Success"
                setupTextColor(R.color.green)
            }
            2 ->  view.apply {
                text = "Failure"
                setupTextColor(R.color.red)
            }
            else ->  view.apply {
                text = "Pending"
                setupTextColor(R.color.yellow)
            }
        }

    }
}
