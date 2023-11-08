package com.branchx.agent.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


open class BaseFragment<B : ViewDataBinding>(private val layout: Int) : Fragment() {

    var progressDialog: Dialog? = null
    private var _binding: B? = null
    val binding : B
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "FragmentLs: onCreateView " + this.javaClass.simpleName)
        _binding = DataBindingUtil.inflate(layoutInflater,layout,container,false)
        return _binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "FragmentLs: onDestroy " + this.javaClass.simpleName)
        _binding = null
    }


    companion object {
        const val TAG = "BaseFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "FragmentLs: onAttach " + this.javaClass.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "FragmentLs: onCreate " + this.javaClass.simpleName)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "FragmentLs: onViewCreated " + this.javaClass.simpleName)
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "FragmentLs: onResume " + this.javaClass.simpleName)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "FragmentLs: onPause " + this.javaClass.simpleName)
    }


    override fun onStop() {
        super.onStop()
        Log.i(TAG, "FragmentLs: onStop " + this.javaClass.simpleName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "FragmentLs: onDestroyView " + this.javaClass.simpleName)
    }


    override fun onDetach() {
        Log.i(TAG, "onDetach " + this.javaClass.simpleName)
        super.onDetach()
    }

}