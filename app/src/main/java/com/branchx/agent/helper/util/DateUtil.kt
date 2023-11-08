package com.branchx.agent.helper.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

    fun datePicker(
        context: Context?,
        disableFutureDate: Boolean = false,
        onDatePicker: (date: String) -> Unit
    ) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar[Calendar.YEAR]
        val month = myCalendar[Calendar.MONTH]
        val day = myCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            context!!,
            OnDateSetListener { view: DatePicker?, year1: Int, mMonthOfYear: Int, dayOfMonth: Int ->
                val monthOfYear = mMonthOfYear + 1
                var m = monthOfYear.toString()
                var d = dayOfMonth.toString()
                if (monthOfYear < 10) m = "0$monthOfYear"
                if (dayOfMonth < 10) d = "0$dayOfMonth"
                onDatePicker("$year1-$m-$d")
            }, year, month, day
        )
        if (disableFutureDate)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    fun currentDate(): String {
        val cDate = Date()
        return SimpleDateFormat("yyyy-MM-dd").format(cDate)
    }


    fun getTimeInMilliSeconds(addHours: Int? = null): Long {

        addHours?.let {
            val date = Date()
            val timeMilli = date.time
            return timeMilli + (3600 * (addHours * 1000))
        } ?: run {
            val date = Date()
            val timeMilli = date.time
            return timeMilli
        }

    }

    fun getTimeAfterMinute(minute: Int): Long {
        val date = Date()
        val timeMilli = date.time
        return timeMilli + (60000 * minute)
    }

    fun checkAfet8Hors(loginMilliSeconds: Long): Boolean {
        return if (loginMilliSeconds > 0L) {
            loginMilliSeconds > getTimeInMilliSeconds()
        } else false

    }

    fun getDateBeforeOneWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

}