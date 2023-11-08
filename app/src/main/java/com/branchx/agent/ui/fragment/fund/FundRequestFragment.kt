package com.branchx.agent.ui.fragment.fund

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.branchx.agent.R
import com.branchx.agent.data.local.AppPreference
import com.branchx.agent.data.model.FundRequestBankResponse
import com.branchx.agent.data.model.FundRequestPaymentModeResponse
import com.branchx.agent.data.model.FundRequestUserTypeResponse
import com.branchx.agent.databinding.FragmentFundRequestBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.helper.util.DateUtil
import com.branchx.agent.helper.util.PickerFragmentUtil
import com.branchx.agent.ui.activity.AuthActivity
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.FundRequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class FundRequestFragment : BaseFragment<FragmentFundRequestBinding>(
    R.layout.fragment_fund_request
) {
    @Inject
    lateinit var appPreference: AppPreference

    private val viewModel: FundRequestViewModel by viewModels()
    private var bankSpinnerDialog: SpinnerDialog? = null
    lateinit var fundRequestUserId : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.btnMakeRequest.setOnClickListener {

            if (!viewModel.validateInput()) return@setOnClickListener
            viewModel.makeRequest()
            //PickerFragmentUtil.pickImage(this)
        }

        binding.btnCancel.setOnClickListener { activity?.onBackPressed() }
        binding.edPaymentDate.setOnClickListener {
            DateUtil.datePicker(requireActivity(), true) {
                viewModel.paymentDate = it
                binding.edPaymentDate.setText(it)
            }
        }

        viewModel.fetchFundRequestInitialData()

        binding.edFile.setOnClickListener {
            PickerFragmentUtil.pickImage(this)
        }


        setupPaymentDate()
        subscribeObservers()
    }

    private fun setupPaymentDate() {
        val currentDate = DateUtil.currentDate()
        binding.edPaymentDate.setText(currentDate)
        viewModel.paymentDate = currentDate
    }


    private fun subscribeObservers() {

        viewModel.fundRequestObs.observe(viewLifecycleOwner, Observer {
            when (it) {

                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireActivity())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        AppDialog.success(requireActivity(), it.data.message, true)
                    } else if(it.data.message.equals("Unverified/ Device")) {
                        logout()
                    } else {
                        logout() }

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)

                }
            }

        })

        viewModel.fundRequestInitialDataObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.apply {
                        progress.root.show()
                        scrollView.hide()
                    }
                }
                is Resource.Success -> {
                    binding.apply {
                        progress.root.hide()
                        scrollView.show()
                    }

                    setupPaymentTo(it.data.userTypeResponse)
                    setupPaymentType(it.data.paymentModeResponse)
                   // setupBankList(it.data.bankResponse)
                    setupBankType(it.data.bankResponse)
                }
                is Resource.Failure -> {
                    binding.apply {
                        progress.root.hide()
                        scrollView.show()
                    }
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })
    }

    private fun setupBankType(it: FundRequestBankResponse) {

        if (it.status == 1) {

            if (it.bankList.isEmpty()) {
                logout()
                return@setupBankType
            }


            val mList = it.bankList.map { it.name }
            binding.spnBankType.setupAdapter(mList.toTypedArray()) { value ->
                viewModel.bankName = it.bankList.find { it.name == value }?.accountNumber ?: ""
                setUpList()
            }

        } else {
            logout() }
    }

    private fun setUpList() {
        viewModel.fundRequestBankResponseObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.apply {
                        progress.root.show()
                        scrollView.hide()
                    }
                }
                is Resource.Success -> {
                    binding.apply {
                        progress.root.hide()
                        scrollView.show()
                    }

                    //setupBankType(it.data.bankResponse)
                }
                is Resource.Failure -> {
                    binding.apply {
                        progress.root.hide()
                        scrollView.show()
                    }
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

    }


    private fun setupBankList(it: FundRequestBankResponse) {

        if (it.status == 1) {

            if (it.bankList.isEmpty()) {
                AppDialog.failure(requireActivity(), "No use type available")
                return@setupBankList
            }


            val mList = it.bankList.map { it.name }
            bankSpinnerDialog =
                SpinnerDialog(requireActivity(), mList as ArrayList<String>, "Search Bank").apply {
                    setCancellable(true)
                    setShowKeyboard(false)
                    bindOnSpinerListener { item, _ ->
                        viewModel.bankName = item
                       // binding.edBank.setText(item)
                    }
                }


        } else AppDialog.failure(requireActivity(), it.message)
    }

    private fun setupPaymentTo(it: FundRequestUserTypeResponse) {
        if (it.status == 1) {

            if (it.userTypes.isEmpty()) {
                logout()
               // AppDialog.failure(requireActivity(), "No use type available")
                return@setupPaymentTo
            }


            val mList = it.userTypes.map { it.mobileNumber }
            binding.spnRequestTo.setupAdapter(mList.toTypedArray()) { value ->
                viewModel.paymentTo = it.userTypes.find { it.mobileNumber == value }?.userId ?: ""
                fundRequestUserId = viewModel.paymentTo
                viewModel.userId = fundRequestUserId
                //viewModel.updateBankList()
                viewModel.fetchFundRequestBankResponseData(fundRequestUserId)
            }


        } else {
            logout()
            //AppDialog.failure(requireActivity(), it.message)
           }
    }


    private fun setupPaymentType(it: FundRequestPaymentModeResponse) {
        if (it.status == 1) {

            if (it.paymentModeList.isEmpty()) {
                logout()
               // AppDialog.failure(requireActivity(), "No use type available")
                return@setupPaymentType
            }
            val mList = it.paymentModeList.map { it.name }
            binding.spnRequestType.setupAdapter(mList.toTypedArray()) { value ->
                viewModel.paymentType = it.paymentModeList.find { it.name == value }?.id ?: ""
                when (value) {
                    "Cash Deposit Machine" -> {
                        viewModel.fieldOneTitle = "Cash Deposit Machine"
                        viewModel.fieldTwoTitle = "Transaction ID"
                    }
                    "Counter Deposit" -> {
                        viewModel.fieldOneTitle = "Branch Name"
                        viewModel.fieldTwoTitle = "Transaction ID"
                    }
                    "NEFT/RTGS/IMPS" -> {
                        viewModel.fieldOneTitle = "Mobile Number"
                        viewModel.fieldTwoTitle = "Utr Number"
                    }
                }
                setupExtraFieldTitle()
            }
        } else {
            logout()
          //  AppDialog.failure(requireActivity(), it.message)
        }

//        binding.edBank.setOnClickListener {
//            bankSpinnerDialog?.showSpinerDialog()
//        }
    }

    private fun setupExtraFieldTitle() {
        binding.tvFieldOneHeader.text = viewModel.fieldOneTitle
        binding.tvFieldTwoHeader.text = viewModel.fieldTwoTitle

        if (viewModel.fieldOneTitle == "Mobile Number") {
            binding.edFieldOne.apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                filters = arrayOf(InputFilter.LengthFilter(10))
            }
        } else {
            binding.edFieldOne.apply {
                inputType = InputType.TYPE_CLASS_TEXT
                filters = arrayOf(InputFilter.LengthFilter(100))
            }
        }

        if (viewModel.fieldTwoTitle == "Mobile Number") {
            binding.edFieldTwo.apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                filters = arrayOf(InputFilter.LengthFilter(10))
            }
        } else {
            binding.edFieldTwo.apply {
                inputType = InputType.TYPE_CLASS_TEXT
                filters = arrayOf(InputFilter.LengthFilter(100))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PickerFragmentUtil.onActivityResult(this, requestCode, resultCode, data) {
            binding.edFile.setText(it.name)
            viewModel.file = it
        }
    }

    companion object {
        fun newInstance() = FundRequestFragment()
    }

    private fun logout() {
        val intent = Intent (activity, AuthActivity::class.java)
        activity?.startActivity(intent)
    }



}