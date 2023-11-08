package com.branchx.agent.ui.activity

import android.content.Intent
import android.os.Bundle
import com.branchx.agent.R
import com.branchx.agent.data.local.LocalDB
import com.branchx.agent.helper.extns.fullScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var localDB : LocalDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fullScreen()
        cleanUpDB()
        delayAndLaunchLoginActivity()
    }

    private fun delayAndLaunchLoginActivity() {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    val loginIntent = Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
            }
        }
        thread.start()
    }

    private fun cleanUpDB(){
        val loginCount = appPreference.loginCount
        if(11%loginCount ==0){
            localDB.deleteAllData()
            appPreference.loginCount =2
        }
        else appPreference.loginCount = loginCount+1
    }
}