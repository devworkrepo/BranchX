package com.branchx.agent.ui.fragment.dmt

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.branchx.agent.R
import com.branchx.agent.data.model.DmtBank
import com.branchx.agent.databinding.FragmentAddBeneficiaryBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.enum.DmtType
import com.branchx.agent.helper.extns.handleNetworkFailure
import com.branchx.agent.helper.extns.hide
import com.branchx.agent.helper.extns.show
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.dmt.AddBeneficiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.List

@AndroidEntryPoint
class AddBeneficiaryFragment :
    BaseFragment<FragmentAddBeneficiaryBinding>(R.layout.fragment_add_beneficiary) {

    private val viewModel: AddBeneficiaryViewModel by viewModels()
    private lateinit var senderName: String
    private lateinit var senderMobile: String
    private lateinit var dmtType: DmtType


    private var bankListDialog: SpinnerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: AddBeneficiaryFragmentArgs by navArgs()
        senderName = args.senderName
        senderMobile = args.senderMobile
        dmtType = args.dmtType
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.customToolbar.let {
            it.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            it.setNavigationOnClickListener { activity?.onBackPressed() }
        }
        binding.toolbar.tvTitle.text = getString(R.string.dmt_one)
        binding.toolbar.ivLogo.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_rupee_indian
            )
        )


        viewModel.dmtType = dmtType
        viewModel.senderMobileNumber = senderMobile
        viewModel.senderName = senderName

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeObservers()

        binding.llSelectBank.setOnClickListener {
            bankListDialog?.showSpinerDialog()
        }
    }


    private fun subscribeObservers() {
        viewModel.bankListObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    setupLayoutVisibility(false)
                }
                is Resource.Failure -> {
                    setupLayoutVisibility()
                }
                is Resource.Success -> {
                    setupLayoutVisibility()
                    if (it.data.status == 1)
                        setupBankList(it.data.bank)
                    else AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }

        viewModel.addBeneficiaryObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext())
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1)
                        AppDialog.success(requireActivity(), it.data.message, goBack = true)
                    else AppDialog.failure(requireActivity(), it.data.message)
                }
            }
        }


        viewModel.onFetchIfscObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = AppDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    binding.edIfscCode.setText(it.data.masterifsccode)

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }

            }
        }
    }

    private fun setupBankList(bank: List<DmtBank>) {

     /*   val sortedMap = bank.associate {
            Pair(it.bankName, it.bankId.toString())
        }.sortByComparator(true)*/

       val a =  bank.map { it.bankName }

        bankListDialog = SpinnerDialog(
            requireActivity(),
            a as ArrayList<String>,
            "Search Bank",
            "Close"
        )

        bankListDialog?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(false)
            spinner.bindOnSpinerListener { item, _ ->
                //viewModel.bankId = sortedMap[item]
                viewModel.bankName = item
                binding.tvBankName.text = item

                viewModel.fetchIfscCode()
            }
        }
    }

    private fun setupLayoutVisibility(visible: Boolean = true) {
        binding.let {
            if (visible) {
                it.contentLayout.show()
                it.layoutProgress.root.hide()
            } else {
                it.contentLayout.hide()
                it.layoutProgress.root.show()
            }
        }
    }

}