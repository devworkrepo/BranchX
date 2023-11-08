package com.branchx.agent.ui.dialog

import android.app.Activity
import android.widget.EditText
import android.widget.LinearLayout
import com.branchx.agent.R
import com.branchx.agent.helper.util.DateUtil
import com.google.android.material.bottomsheet.BottomSheetDialog

object FilterDialog {

    private val currentDate = DateUtil.currentDate()

    var fromDate: String = currentDate
    var toDate: String = currentDate

    fun commonDateFilter(
        activity: Activity,
        onSearchClick: (
            fromDate: String,
            toDate: String) -> Unit
    ) = setupDialog(activity).apply {
        val llSearch = findViewById<LinearLayout>(R.id.ll_search)
        llSearch?.setOnClickListener {
            dismiss()
            onSearchClick(fromDate, toDate)
        }
    }

    private fun setupDialog(
        activity: Activity
    ): BottomSheetDialog {


        //init dialog
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter_dialog)

        //init view
        val edFromDate = bottomSheetDialog.findViewById<EditText>(R.id.ed_from_date)
        val edToDate = bottomSheetDialog.findViewById<EditText>(R.id.ed_to_date)


        //setup date data
        edFromDate?.setText(currentDate)
        edToDate?.setText(currentDate)


        //onClickListener
        edFromDate?.setOnClickListener {
            DateUtil.datePicker(activity, disableFutureDate = true) {
                fromDate = it
                edFromDate.setText(it)
            }
        }
        edToDate?.setOnClickListener {
            DateUtil.datePicker(activity, disableFutureDate = true) {
                toDate = it
                edToDate.setText(it)
            }
        }

        bottomSheetDialog.show()
        return bottomSheetDialog
    }

}