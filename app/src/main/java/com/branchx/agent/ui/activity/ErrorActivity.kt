package com.branchx.agent.ui.activity

import android.os.Bundle
import com.branchx.agent.R
import com.branchx.agent.databinding.ActivityErrorBinding

import com.branchx.agent.helper.extns.LottieType
import com.branchx.agent.helper.extns.launchActivity
import com.branchx.agent.helper.extns.set
import com.branchx.agent.helper.extns.setupTextColor


class ErrorActivity : BaseActivity() {

    private  var isTransactionApi   = false


    private lateinit var type: String
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var action: String
    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isTransactionApi = appPreference.isApiTransaction

        type = intent.getStringExtra(TYPE)!!
        title = intent.getStringExtra(TITLE)!!
        description = intent.getStringExtra(DESCRIPTION)!!
        action = intent.getStringExtra(ACTION) ?: "default_action"

        when (type) {
            ROOT -> setUpViewForRootCondition()
            NETWORK_EXCEPTION ->setUpViewForNetworkException()
        }

        binding.let {
            it.tvTitle.text = title
            it.tvDescription.text = description
        }
    }
    private fun setUpViewForNetworkException() {

        val networkExceptionType = intent.getStringExtra(NetworkExceptions.NETWORK_EXCEPTION_TYPE)
        binding.tvTitle.setupTextColor(R.color.red)
        binding.lottieView.set(LottieType.ALERT)

        if(isTransactionApi){
            binding.tvTitle.setupTextColor( R.color.yellow)
            binding.lottieView.set(LottieType.PENDING)
            binding.tvTitle.text = getString(R.string.please_check_transaction_report)
            binding.tvDescription.text = getString(R.string.transaction_error_message)
        }
        else when (networkExceptionType) {
            NetworkExceptions.NO_INTERNET -> binding.lottieView.set(LottieType.NO_INTERNET)
            NetworkExceptions.INTERNAL_SERVER_ERROR -> binding.lottieView.set(LottieType.SERVER)
            NetworkExceptions.TIME_OUT_EXCEPTION ->binding.lottieView.set(LottieType.TIME_OUT)
        }
    }

    private fun setUpViewForRootCondition() {
        binding.lottieView.set(LottieType.WARNING)
        binding.tvTitle.setupTextColor( R.color.red)
    }


    override fun onBackPressed() {
        when(action){
            NAVIGATE_TO_LOGIN->
                launchActivity(AuthActivity::class.java,forgotAll = true)
            NAVIGATE_TO_MAIN->
                launchActivity(MainActivity::class.java,forgotAll = true)
            else -> {
                if(type == NETWORK_EXCEPTION && isTransactionApi)
                    launchActivity(MainActivity::class.java,forgotAll = true)
                else super.onBackPressed()
            }
        }
    }

    companion object {
        //TAGS
        const val TYPE = "type"
        const val TITLE = "title"
        const val DESCRIPTION= "description"
        const val ACTION = "action"
        //TYPES
        const val ROOT = "root"
        const val NETWORK_EXCEPTION = "network_exception"
        //ACTIONS
        const val NAVIGATE_TO_MAIN ="navigate_to_main"
        const val NAVIGATE_TO_LOGIN ="navigate_to_login"
    }

    class NetworkExceptions{
        companion object{
            const val NETWORK_EXCEPTION_TYPE = "network_exception_type"
            const val INTERNAL_SERVER_ERROR = "internal_server_error"
            const val TIME_OUT_EXCEPTION = "time_out_exception"
            const val UNAUTHORIZED = "unauthorized"
            const val NO_INTERNET = "no_internet"
            const val OTHER = "other"
        }
    }
}