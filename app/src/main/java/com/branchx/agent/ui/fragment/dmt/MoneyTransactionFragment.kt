package com.branchx.agent.ui.fragment.dmt

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.data.model.SenderInfo
import com.branchx.agent.databinding.FragmentMoneyTransactionBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.MoneyTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoneyTransactionFragment :
    BaseFragment<FragmentMoneyTransactionBinding>(R.layout.fragment_money_transaction) {

    private val viewModel: MoneyTransactionViewModel by viewModels()

    @Inject
    lateinit var appPreference: AppPreference

    private lateinit var senderInfo: SenderInfo
    private lateinit var strAmount: String
    private lateinit var beneficiaryInfo: BeneficiaryInfo
    private var shouldBackPressHandle: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moneyTransactionArgs: MoneyTransactionFragmentArgs by navArgs()
        senderInfo = moneyTransactionArgs.senderInfo
        beneficiaryInfo = moneyTransactionArgs.beneficiaryInfo
        strAmount = moneyTransactionArgs.strAmount

        activity?.onBackPressedDispatcher?.addCallback {
            isEnabled = shouldBackPressHandle
            if (isEnabled) {
                activity?.makeToast("transaction begin proceed! don't press back button")
            } else activity?.onBackPressed()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.amount = strAmount
        //setup binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.beneficiaryId = beneficiaryInfo.id
        viewModel.remitterMobile = senderInfo.mobileNumber.toString()
        viewModel.beneficiaryInfo = beneficiaryInfo
        viewModel.senderInfo = senderInfo

        binding.tvWalletBalance.text= appPreference.user.mainBalance

        //setup toolbar
        val toolbarAmount = appPreference.user.mainBalance
        val toolbarTitle = requireContext().resources.getString(R.string.money_transfer)
        setupCustomToolbarTitleAmount(binding.toolbar, toolbarAmount, toolbarTitle)

        binding.let {
            it.tvSender.text = senderInfo.getBeneficiaryName()
            it.tvBeneficiaryName.text = beneficiaryInfo.name
            it.tvAccountNumber.text = beneficiaryInfo.Benber
            it.tvIfscCode.text = beneficiaryInfo.ifsc
            it.tvBankName.text = beneficiaryInfo.bankName
            it.edAmount.setText(strAmount)
            it.edAmount.isEnabled = false
            it.btnCancel.setOnClickListener { activity?.onBackPressed() }

            it.edConfirmAmount.afterTextChanged {
                onAmountChange(
                    edAmount = binding.edAmount,
                    tvAmountInWord = binding.tvAmountInWord,
                    balance = appPreference.user.mainBalance
                )
            }
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        fragObserver(viewModel.transactionObs) {
            when (it) {
                is Resource.Loading -> transactionProgressVisibility(true)
                is Resource.Failure -> {
                    transactionProgressVisibility(false)
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    transactionProgressVisibility(false)

                    if(it.data.status == 1){
                        findNavController().navigate(
                            R.id.action_moneyTransactionFragment_to_moneyTransactionResponseFragment,
                            MoneyTransactionResponseFragmentArgs(
                                transactionResponse = it.data,
                                beneficiaryInfo = beneficiaryInfo,
                                senderInfo = senderInfo,
                                amount = strAmount
                            ).toBundle()
                        )
                    } else if(it.data.status == 3){
                        AppDialog.pending(requireActivity(),it.data.message)
                    } else {
                        AppDialog.failure(requireActivity(),it.data.message)
                    }


                }
            }
        }
    }


    private fun transactionProgressVisibility(visible: Boolean) {
        shouldBackPressHandle = visible
        binding.let {
            if (visible) {
                it.viewTransparent.show()
                it.cvProgressTransaction.show()
                it.mainLayout.isUserInteractionEnabled(false)
            } else {
                it.viewTransparent.hide()
                it.cvProgressTransaction.hide()
                it.mainLayout.isUserInteractionEnabled(true)
            }
        }

    }
}