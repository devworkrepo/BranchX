package com.branchx.agent.ui.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.branchx.agent.R
import com.branchx.agent.data.model.BeneficiaryInfo
import com.branchx.agent.databinding.ListBeneficiaryBinding
import com.branchx.agent.helper.extns.*
import com.branchx.agent.ui.viewmodel.dmt.BeneficiaryListViewModel


var animationPlaybackSpeed: Double = 0.8

class BeneficiaryListAdapter(
    context: Context,
    private val userBalance: String,
    viewModel: BeneficiaryListViewModel
) : BaseRecyclerViewAdapter<BeneficiaryInfo, ListBeneficiaryBinding>(R.layout.list_beneficiary) {


    private val originalBg: Int by bindColor(context, R.color.white)
    private val expandedBg: Int by bindColor(context, R.color.bg_expanded)
    val beneficiary: BeneficiaryListViewModel = viewModel

    private val listItemHorizontalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_horizontal_padding
    )
    private val listItemVerticalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_vertical_padding
    )

    private val originalWidth = context.screenWidth - 24.dp
    private val expandedWidth = context.screenWidth - 16.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()

    private var expandedModel: BeneficiaryInfo? = null
    private lateinit var recyclerView: RecyclerView

    private var isScaledDown = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }


    override fun onViewAttachedToWindow(holder: Companion.BaseViewHolder<ListBeneficiaryBinding>) {
        super.onViewAttachedToWindow(holder)


        // get originalHeight & expandedHeight if not gotten before
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.binding
            holder.binding.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.binding.expandView.isVisible = true
                view.doOnPreDraw {
                    expandedHeight = view.height
                    holder.binding.expandView.isVisible = false
                }
            }
        }
    }


    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<ListBeneficiaryBinding>,
        position: Int
    ) {
        val model = items[position]
        holder.binding.beneficiary = model

        if(model.BeneVrfy == "No"){
            holder.binding.tvValidateStatus.hide()
        }
        else holder.binding.tvValidateStatus.show()
        if(model.BeneVrfy == "Yes"){
            holder.binding.btnVerify.hide()
        }
        else holder.binding.btnVerify.show()

        expandItem(holder.binding, model == expandedModel, animate = false)
        scaleDownItem(holder.binding, position, isScaledDown)

        holder.binding.edAmount.afterTextChanged {
            onAmountChange(
                edAmount = holder.binding.edAmount,
                tvAmountInWord = holder.binding.tvAmountInWord,
                button = holder.binding.btnTransfer,
                balance = userBalance
            )
        }

        holder.binding.btnDelete.setOnClickListener {
            deleteBeneListener?.invoke(model)
        }

        holder.binding.btnVerify.setOnClickListener {
            verifyBeneListener?.invoke(model)
        }



        holder.binding.btnTransfer.setOnClickListener {
            onTransferButtonClick( model, holder.binding.edAmount.text.toString())
        }

        holder.binding.cardContainer.setOnClickListener {
            if (expandedModel == null) {

                // expand clicked view
                expandItem(holder.binding, expand = true, animate = true)
                expandedModel = model
            } else if (expandedModel == model) {

                // collapse clicked view
                expandItem(holder.binding, expand = false, animate = true)
                expandedModel = null
            } else {

                // collapse previously expanded view
                val expandedModelPosition = items.indexOf(expandedModel!!)
                val oldBinding =
                    recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? Companion.BaseViewHolder<ListBeneficiaryBinding>


                if (oldBinding != null) expandItem(
                    oldBinding.binding,
                    expand = false,
                    animate = true
                )

                // expand clicked view
                expandItem(holder.binding, expand = true, animate = true)
                expandedModel = model
            }
        }
    }

    private fun expandItem(
        binding: ListBeneficiaryBinding,
        expand: Boolean = true,
        animate: Boolean = true
    ) {
        if (animate) {
            val animator = getValueAnimator(
                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(binding, progress) }

            if (expand) animator.doOnStart { binding.expandView.isVisible = true }
            else animator.doOnEnd { binding.expandView.isVisible = false }

            animator.start()
        } else {

            // show expandView only if we have expandedHeight (onViewAttached)
            binding.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(binding, if (expand) 1f else 0f)
        }
    }


    private fun setExpandProgress(binding: ListBeneficiaryBinding, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            binding.cardContainer.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        binding.cardContainer.layoutParams.width =
            (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        binding.cardContainer.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
        binding.cardContainer.requestLayout()

        binding.chevron.rotation = 90 * progress
    }

    private fun scaleDownItem(
        binding: ListBeneficiaryBinding,
        position: Int,
        isScaleDown: Boolean
    ) {
        setScaleDownProgress(binding, position, if (isScaleDown) 1f else 0f)
    }

    private fun setScaleDownProgress(
        holder: ListBeneficiaryBinding,
        position: Int,
        progress: Float
    ) {
        val itemExpanded = position >= 0 && items[position] == expandedModel
        holder.cardContainer.layoutParams.apply {
            width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
//            log("width=$width, height=$height [${"%.2f".format(progress)}]")
        }
        holder.cardContainer.requestLayout()

        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.scaleContainer.setPadding(
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

    }


    private fun onTransferButtonClick(
        model: BeneficiaryInfo,
        strAmount: String
    ) {
        onTransferButtonClickCallback?.invoke(model,strAmount)
    }

    var onTransferButtonClickCallback: ((model: BeneficiaryInfo,strAmount : String) -> Unit)? = null
    var deleteBeneListener: ((model: BeneficiaryInfo) -> Unit)? = null
    var verifyBeneListener: ((model: BeneficiaryInfo) -> Unit)? = null

}