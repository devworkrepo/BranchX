package com.branchx.agent.ui.fragment.settlement

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.databinding.FragmentSettlementNewBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.util.ViewUtil
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.SettlementViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettlementNewFragment : BaseFragment<FragmentSettlementNewBinding>(
    R.layout.fragment_settlement_new
) {
    @Inject
    lateinit var appPreference: AppPreference
    lateinit var viewModel: SettlementViewModel
    private var key: Int = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getInt("key") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(key.toString(), SettlementViewModel::class.java)


        setupRadioButtons()

        ViewUtil.resetErrorOnTextInputLayout(binding.tilAmount)


        binding.btnProceed.setOnClickListener {
            viewModel.amount = binding.edAmount.text.toString()
            if (viewModel.amount.isNotEmpty()) {
                binding.tilAmount.isErrorEnabled = false
                viewModel.proceedSettlementRequest()

            } else {
                binding.tilAmount.apply {
                    this.error = "Enter valid amount"
                    this.isErrorEnabled = true
                }
            }

        }


        binding.tvAepsBalance.text = appPreference.user.aepsBalance
        binding.tvMatmBalance.text = appPreference.user.matmBalance
        subscribeObservers()
    }

    private fun setupRadioButtons() {

        //setup wallet type
        binding.rbAeps1.isChecked = true

        binding.rbAeps1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)  viewModel.walletType = "Aeps1"
        }

        binding.rbAeps2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)  viewModel.walletType = "Aeps2"
        }

        binding.rbMatm.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)  viewModel.walletType = "Matm"
        }


        //setup transfer type
        binding.rbBankDirect.isChecked = true

        binding.rbBankDirect.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)    viewModel.transferType = "BD"
        }

        binding.rbBankManual.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)     viewModel.transferType = "BM"
        }

        binding.rbWalletManual.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)     viewModel.transferType = "WM"
        }



        //setup transfer mode
        binding.rbImps.isChecked = true

        binding.rbImps.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)    viewModel.mType = "IMPS"
        }
        binding.rbNeft.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)    viewModel.mType = "NEFT"
        }

    }


    private fun subscribeObservers() {
        viewModel.onSettlementObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(
                        requireContext(),
                        title = "Processing...",
                        cancelable = false
                    )
                }
                is Resource.Success->{
                    progressDialog?.dismiss()

                    if(it.data.status == 1){
                        AppDialog.success(requireActivity(),it.data.message,true)
                    }
                    else AppDialog.failure(requireActivity(),it.data.message)
                }
                is Resource.Failure ->{
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })
    }


    companion object {
        fun newInstance(key: Int) = SettlementNewFragment().apply {
            this.arguments = bundleOf("key" to key)
        }
    }
}