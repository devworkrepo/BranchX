package com.branchx.agent.ui.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.branchx.agent.R
import com.branchx.agent.helper.extns.LottieType
import com.branchx.agent.helper.extns.set
import com.branchx.agent.helper.extns.setupTextColor
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import java.util.*

object AppDialog {





    inline fun imageAndCameraChooser(
        context: Context,
        crossinline onGallery: () -> Unit,
        crossinline onCamera: () -> Unit
    ) {
        AlertDialog.Builder(context).apply {
            setTitle("Choose Camera or Gallery")
            setIcon(R.drawable.ic_baseline_attach_file_24)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setItems(arrayOf("Gallery", "Camera")) { _, which ->
                when (which) {
                    0 -> onGallery()
                    1 -> onCamera()

                }
            }
            show()
        }
    }


    fun matmResponse(
        context: Context,
        status: Boolean,
        message: String,
        transactionAmount: String,
        balanceAmount: String,
        bankRnn: String,
        transactionType: String,
        cardNumber: String,
        bankName: String
    ): Dialog {
        val dialog = dialogConfiguration(context, R.layout.dialog_matm_response)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = message
        dialog.findViewById<TextView>(R.id.tv_transaction_amount).text = transactionAmount
        dialog.findViewById<TextView>(R.id.tv_balance_amount).text = balanceAmount
        dialog.findViewById<TextView>(R.id.tv_bank_rrn).text = bankRnn
        dialog.findViewById<TextView>(R.id.tv_trans_type).text = transactionType
        dialog.findViewById<TextView>(R.id.tv_bank_name).text = bankName
        dialog.findViewById<TextView>(R.id.tv_card_number).text = cardNumber
        dialog.findViewById<TextView>(R.id.tv_transaction_status).text =
            if (status) "Success" else "Failed"


        val lottieAnimationView: LottieAnimationView =
            dialog.findViewById(R.id.lottie_transaction_status)
        if (status) {
            tvMessage.setTextColor(ContextCompat.getColor(context, R.color.green))
            lottieAnimationView.set(LottieType.SUCCESS)
        } else {
            tvMessage.setTextColor(ContextCompat.getColor(context, R.color.red))
            lottieAnimationView.set(LottieType.FAILED)
        }

        dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener { dialog.dismiss() }

        dialog.show()
        return dialog
    }

    fun transactionProgress(context: Context): Dialog {
        val dialog = Dialog(context, R.style.Theme_Mego_Pay)
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.dialog_transction_fs)
            val window: Window? = it.window
            val wlp: WindowManager.LayoutParams? = window?.attributes
            window?.statusBarColor = ContextCompat.getColor(context, R.color.purple_700);

            wlp?.gravity = Gravity.CENTER
            // wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv())
            window?.attributes = wlp
            it.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            it.show()
        }
        return dialog

    }

    fun otp(
        context: Context,
        title: String = "Loading...",
        message: String = "Otp has sent to remitter mobile number"
    ): Dialog {
        val dialog = dialogConfigurationLinearlayout(
            context,
            R.layout.dialog_progress,
            wrapWith = true,
        )
        dialog.findViewById<TextView>(R.id.tv_title).text = title
        dialog.show()
        return dialog
    }

    fun progress(
        context: Context,
        title: String = "Loading...",
        cancelable: Boolean = true
    ): Dialog {
        val dialog = dialogConfigurationLinearlayout(
            context,
            R.layout.dialog_progress,
            wrapWith = true,
            cancelable = cancelable
        )
        dialog.findViewById<TextView>(R.id.tv_title).text = title
        dialog.show()
        return dialog
    }

    fun success(context: Activity, message: String, goBack: Boolean = false): Dialog =
        statusDialog(context, message, STATUS.SUCCESS, goBack)

    fun failure(context: Activity, message: String, goBack: Boolean = false): Dialog =
        statusDialog(context, message, STATUS.FAILED, goBack)

    fun pending(context: Activity, message: String, goBack: Boolean = false): Dialog =
        statusDialog(context, message, STATUS.PENDING, goBack)


    private fun statusDialog(
        context: Activity,
        message: String,
        type: STATUS,
        goBack: Boolean
    ): Dialog {
        val dialog =
            dialogConfiguration(context, R.layout.dialog_response_status, wrapWith = false)
        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        val lottieView = dialog.findViewById<LottieAnimationView>(R.id.lottie_view)
        val btnDone = dialog.findViewById<Button>(R.id.btn_done)

        tvMessage.text = message

        btnDone.setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener {
            if (goBack) context.onBackPressed()
        }

        when (type) {
            STATUS.SUCCESS -> {
                tvMessage.setupTextColor(R.color.green_color)
                lottieView.set(LottieType.SUCCESS)
            }
            STATUS.FAILED -> {
                tvMessage.setupTextColor(R.color.red)
                lottieView.set(LottieType.FAILED)
            }
            STATUS.PENDING -> {
                tvMessage.setupTextColor(R.color.blue)
                lottieView.set(LottieType.ALERT)
                lottieView.addValueCallback( KeyPath("**"),
                    LottieProperty.COLOR_FILTER
                ) { PorterDuffColorFilter(Color.parseColor("#0645AD"), PorterDuff.Mode.SRC_ATOP) }
            }
        }

        dialog.show()
        return dialog
    }

    enum class STATUS {
        SUCCESS, FAILED, PENDING
    }

    fun openCameraGallery(context: Context): Dialog {
        return dialogConfiguration(
            context,
            R.layout.dialog_open_camera_gallery
        )
    }

}


fun dialogConfiguration(
    context: Context, layout: Int,
    cancelable: Boolean = true,
    wrapWith: Boolean = false
): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(layout)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(cancelable)
    val contentLayout: CardView = dialog.findViewById(R.id.card_view)
    val displayMetrics = context.resources.displayMetrics
    val paramsLcl = contentLayout.layoutParams as FrameLayout.LayoutParams
    if (!wrapWith) {
        val widthLcl = (displayMetrics.widthPixels * 0.90f).toInt()
        paramsLcl.width = widthLcl
    }
    paramsLcl.gravity = Gravity.CENTER
    contentLayout.layoutParams = paramsLcl
    Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    if (!dialog.isShowing)
        dialog.show()
    return dialog
}

private fun dialogConfigurationLinearlayout(
    context: Context, layout: Int,
    cancelable: Boolean = true,
    wrapWith: Boolean = false
): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(layout)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(cancelable)
    val contentLayout: LinearLayout = dialog.findViewById(R.id.contentLayout)
    val displayMetrics = context.resources.displayMetrics
    val paramsLcl = contentLayout.layoutParams as FrameLayout.LayoutParams
    if (!wrapWith) {
        val widthLcl = (displayMetrics.widthPixels * 0.90f).toInt()
        paramsLcl.width = widthLcl
    }
    paramsLcl.gravity = Gravity.CENTER
    contentLayout.layoutParams = paramsLcl
    Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    if (!dialog.isShowing)
        dialog.show()
    return dialog
}