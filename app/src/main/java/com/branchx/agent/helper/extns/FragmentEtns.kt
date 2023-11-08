package com.branchx.agent.helper.extns

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.branchx.agent.R
import com.branchx.agent.databinding.CustomToolbar2Binding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.ui.activity.MainActivity

fun Fragment?.setupCustomToolbarTitleAmount(
    mBinding: CustomToolbar2Binding,
    amount : String,
    title : String
)  {
    this?.run {
        mBinding.customToolbar.let { tb->
            tb.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            tb.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
        mBinding.tvTitle.text = title
    }
}

fun Fragment.removeCurrent(){
    activity?.supportFragmentManager?.beginTransaction()
            ?.remove(this)?.commit()

}


inline  fun <T : Resource<T>> Fragment.fragObserver(mLiveData: LiveData<T>, crossinline observer : (T)->Unit){
    mLiveData.observe(viewLifecycleOwner,{
        observer(it)
    })
}

inline fun Fragment.backPressHandler(crossinline handler : (OnBackPressedCallback)->Unit){
    requireActivity().onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) {
        handler(this)
    }
}

fun Fragment.gotoMainActivity(){

    activity?.let {
        val intent =  Intent(it,MainActivity::class.java)
        it.startActivity(intent)
        activity?.finish()
    }


}

fun Fragment.setupToolbar(toolbar: Toolbar, title : String){

    toolbar.title = title
    (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
    (activity as AppCompatActivity?)?. supportActionBar?.let {
        it.setDisplayHomeAsUpEnabled(true)
        it.setDisplayShowHomeEnabled(true)
    }
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
}

