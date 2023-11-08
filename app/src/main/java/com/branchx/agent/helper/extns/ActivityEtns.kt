package com.branchx.agent.helper.extns

import android.app.Activity
import android.content.Intent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.branchx.agent.ui.activity.AuthActivity
import com.branchx.agent.ui.activity.MainActivity

fun Activity.fullScreen() {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.gotoMainActivity(launchOrigin : String?=null){
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    launchOrigin?.let {
        intent.putExtra("launchOrigin",launchOrigin)
    }
    startActivity(intent)
}

fun Activity.gotoLoginActivity(){
    val intent = Intent(this, AuthActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}



fun AppCompatActivity.setupToolbar(toolbar: Toolbar, title : String, backEnable : Boolean = true){

    toolbar.title = title
    setSupportActionBar(toolbar)
    if(backEnable)
    {
        supportActionBar?.let {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}
