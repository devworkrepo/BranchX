package com.branchx.agent.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.branchx.agent.R
import com.branchx.agent.data.model.support.SupportContact
import com.branchx.agent.databinding.FragmentSupportContactBinding
import com.branchx.agent.helper.api.Resource
import com.branchx.agent.helper.extns.*
import com.branchx.agent.ui.adapter.SupportContactAdapter
import com.branchx.agent.ui.dialog.AppDialog
import com.branchx.agent.ui.fragment.BaseFragment
import com.branchx.agent.ui.viewmodel.SupportContactViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportContactFragment :
    BaseFragment<FragmentSupportContactBinding>(R.layout.fragment_support_contact) {

    private val viewModel: SupportContactViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchSupportNumbers()
        subscribers()
    }

    private fun subscribers() {
        viewModel.supportNumberObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> binding.run {
                    this.progress.root.show()
                    this.recyclerView.hide()
                    this.layoutNoDataFound.rootLayout.hide()
                }
                is Resource.Success -> {
                    binding.progress.root.hide()
                    if (it.data.status == 1) setupRecyclerView(it.data.contacts)
                    else AppDialog.failure(
                        requireActivity(),
                        it.data.message.toString(),
                        goBack = true
                    )
                }
                is Resource.Failure -> {
                    binding.progress.root.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun setupRecyclerView(contacts: List<SupportContact>?) {
        if (contacts == null || contacts.isEmpty()) binding.run {
            this.layoutNoDataFound.rootLayout.show()
            this.recyclerView.hide()
        }
        binding.recyclerView.setup().adapter = SupportContactAdapter().apply {
            this.addItems(ArrayList(contacts!!))
        }


    }

    companion object {
        fun newInstance() = SupportContactFragment()
    }
}