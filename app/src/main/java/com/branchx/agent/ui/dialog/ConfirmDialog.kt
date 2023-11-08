package com.branchx.agent.ui.dialog

import android.app.Dialog
import android.content.Context
import android.widget.*
import com.branchx.agent.R
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.show
import com.branchx.agent.helper.util.ViewUtil
import com.google.android.material.textfield.TextInputLayout

object ConfirmDialog {


    fun aepsTransactionConfirmation(
        context: Context,
        aadhaarNumber: String,
        transactionType: String,
        amount: String,
        bankName: String
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_aeps_transaction_confirmation).apply {

            findViewById<TextView>(R.id.tv_aadharNumber).text = aadhaarNumber
            findViewById<TextView>(R.id.tv_transaction_type).text = transactionType
            findViewById<TextView>(R.id.tv_amount).text = amount
            findViewById<TextView>(R.id.tv_bank_name).text = bankName


            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }

            val llAmount = findViewById<LinearLayout>(R.id.ll_amount)

            when (transactionType) {
                "Balance Enquiry", "Mini Statement" ->llAmount.hide()
                else -> llAmount.show()
            }




            show()
        }
    }

    fun deleteConfirmation(
        context: Context,
        message: String = "You are sure! to delete this beneficiary",
        onConfirm: (String) -> Unit
    ): Dialog {
        return dialogConfiguration(context, R.layout.dialog_delete_confirmation).apply {

            var otp = ""

            findViewById<TextView>(R.id.tv_message).text = message
            findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }

            val edOtp = findViewById<EditText>(R.id.ed_otp)
            val tilOtp = findViewById<TextInputLayout>(R.id.til_otp)

            ViewUtil.resetErrorOnTextInputLayout(tilOtp)

            val btnConfirm = findViewById<Button>(R.id.btn_proceed)
            btnConfirm.setOnClickListener {

                otp = edOtp.text.toString()
                if (otp.length == 4) {
                    dismiss()
                    onConfirm(otp)
                } else {
                    tilOtp.isErrorEnabled = true
                    tilOtp.error = "Enter valid 4 digits Otp"

                }


            }





            show()
        }
    }


}