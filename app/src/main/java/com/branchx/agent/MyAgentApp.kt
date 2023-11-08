package com.branchx.agent

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyAgentApp : Application() {

    companion object {
        const val TAG = "MyAgentApp"
    }

    override fun onCreate() {
        super.onCreate()
        registerForCallBacks()
    }


    private fun registerForCallBacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityCreated " + activity.javaClass.simpleName
                )
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityStarted " + activity.javaClass.simpleName
                )
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityResumed " + activity.javaClass.simpleName
                )
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityPaused " + activity.javaClass.simpleName
                )
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityStopped " + activity.javaClass.simpleName
                )
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivitySaveInstanceState " + activity.javaClass.simpleName
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d(
                    TAG,
                    "ActivityLs: onActivityDestroyed " + activity.javaClass.simpleName
                )
            }
        })
    }

}
