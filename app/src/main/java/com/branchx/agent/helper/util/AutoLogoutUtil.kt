package com.branchx.agent.helper.util

import android.app.ActivityManager
import android.content.Context
import java.util.*


class AutoLogoutUtil constructor(context: Context) {
    private var timer: Timer? = null
    var isSessionTimeout = false
    fun startUserSession() {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                isSessionTimeout = true
            }
        }, 60000 * 10.toLong())
    }

    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun isAppInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var runningProcesses: List<ActivityManager.RunningAppProcessInfo>? = null
        runningProcesses = am.runningAppProcesses
        if (runningProcesses != null) {
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        }
        return isInBackground
    }
    fun logout() {
        /*AppPreferences(context).clearDataOnLogout()
        AppPreferences(context).loginTimeFuture8Hours = 0L
        isSessionTimeout = false
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)*/
    }
}

