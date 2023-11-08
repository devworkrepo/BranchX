package com.branchx.agent.ui.fragment.dmt

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.branchx.agent.R
import com.branchx.agent.databinding.FragmentMoneyTransactionResponseBinding
import com.branchx.agent.helper.extns.*
import com.branchx.agent.ui.adapter.DmtResponseAdapter
import com.branchx.agent.ui.fragment.BaseFragment
import com.airbnb.lottie.LottieAnimationView

class MoneyTransactionResponseFragment :
    BaseFragment<FragmentMoneyTransactionResponseBinding>(R.layout.fragment_money_transaction_response) {

    private lateinit var lottieAnimationView: LottieAnimationView
    private val args: MoneyTransactionResponseFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backPressHandler {
            gotoMainActivity()
        }


        lottieAnimationView = view.findViewById(R.id.lottie_transaction_status)

        binding.apply {
            tvAccountNumber.text = args.beneficiaryInfo.Benber
            tvBeneficiaryName.text = args.beneficiaryInfo.name
            tvAmount.text = args.amount
            tvBankName.text = args.transactionResponse.bankName


            val dmtTransactionResponse = args.transactionResponse
            /* recyclerView.run {
                 layoutManager = LinearLayoutManager(requireActivity())
                 setHasFixedSize(false)
                 val aAdapter = DmtResponseAdapter()
                 aAdapter.context = activity
                 aAdapter.addItems(ArrayList(transactionList))
                 this.adapter = aAdapter
             }*/

            var pendingCount: Int = 0
            var successCount: Int = 0
            var failureCount: Int = 0
            /*for (transaction in transactionList) {
                when (transaction.status) {
                    1 -> successCount += 1
                    2 -> failureCount += 1
                    else -> pendingCount += 1
                }
            }*/

            when (dmtTransactionResponse.status) {
                0, 2 -> {
                    activity?.window?.statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.red)
                    lottieTransactionStatus.set(LottieType.FAILED)
                    tvStatus.text = activity?.text(R.string.failure)
                    tvStatus.setupBGColor(R.color.red)
                    llHome.setupBGColor(R.color.red)
                }

                1 -> {
                    activity?.window?.statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.green)
                    lottieTransactionStatus.set(LottieType.SUCCESS)
                    tvStatus.text = activity?.text(R.string.success)
                    tvStatus.setupBGColor(R.color.green)
                    llHome.setupBGColor(R.color.green)
                }

                3 -> {
                    activity?.window?.statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.yellow)
                    lottieTransactionStatus.set(LottieType.PENDING)
                    tvStatus.text = activity?.text(R.string.pending)
                    tvStatus.setupBGColor(R.color.yellow)
                    llHome.setupBGColor(R.color.yellow)
                }

                /*successCount > 0 && successCount < transactionList.size -> {
                    activity?.window?.statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.purple_500)
                    lottieTransactionStatus.set(LottieType.SUCCESS)
                    tvStatus.text = activity?.text(R.string.some_transaction_are_success)
                    tvStatus.setupBGColor(R.color.purple_500)
                    llHome.setupBGColor(R.color.purple_500)
                }*/
            }
        }

        binding.llHome.setOnClickListener { activity?.gotoMainActivity() }


        /*  when(transactionResponse.status){
              1 -> {
                  activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)
                  lottieTransactionStatus.set(LottieType.SUCCESS)
                  tvStatus.text = activity?.text(R.string.success)
                  tvStatus.setupBGColor(R.color.green)
                  llHome.setupBGColor(R.color.green)
              }
              2 -> {
                  activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.red)
                  lottieTransactionStatus.set(LottieType.FAILED)
                  tvStatus.text = activity?.text(R.string.failure)
                  tvStatus.setupBGColor( R.color.red)
                  llHome.setupBGColor( R.color.red)
              }

              else ->{
                  activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)
                  lottieTransactionStatus.set(LottieType.PENDING)
                  tvStatus.text = activity?.text(R.string.pending)
                  tvStatus.setupBGColor(R.color.yellow)
                  llHome.setupBGColor(R.color.yellow)
              }
          }*/
    }


}
