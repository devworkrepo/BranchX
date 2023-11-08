package com.branchx.agent.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.branchx.agent.helper.api.Exceptions
import com.branchx.agent.helper.util.AppConstants
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


open class BaseViewModel() : ViewModel() {

    val errMessageObs = MutableLiveData(AppConstants.EMPTY)
    val errMessageSingleObs = MutableLiveData(AppConstants.EMPTY)

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody().toString()
            val message = StringBuilder()

            try {
                message.append(JSONObject(error).getString("message"))
            } catch (e: JSONException) {
            }
            message.append("Error Code: ${response.code()}")
            throw Exceptions.ApiException(message.toString())
        }
    }
}