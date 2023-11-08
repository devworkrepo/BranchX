package com.branchx.agent.ui.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.branchx.agent.R
import com.branchx.agent.data.model.notification.Notification
import com.branchx.agent.helper.extns.setup
import com.branchx.agent.ui.adapter.NotificationAdapter

object DialogNotification {

    operator fun invoke(context: Context,notification: List<Notification>) {
        showNotificationDialog(context).apply {
            this.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
                dismiss()
            }
            this.findViewById<RecyclerView>(R.id.recycler_view).setup().adapter=
                NotificationAdapter().apply {
                    addItems(ArrayList(notification))
                }
        }
    }

    private fun showNotificationDialog(context: Context): Dialog {
        val dialog = Dialog(context, R.style.Theme_Mego_Pay)
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.dialog_notification)
            val window: Window? = it.window
            val wlp: WindowManager.LayoutParams? = window?.attributes
            window?.statusBarColor = ContextCompat.getColor(context, R.color.purple_700);

            wlp?.gravity = Gravity.CENTER
            window?.attributes = wlp
            it.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            it.show()
        }
        return dialog
    }
}