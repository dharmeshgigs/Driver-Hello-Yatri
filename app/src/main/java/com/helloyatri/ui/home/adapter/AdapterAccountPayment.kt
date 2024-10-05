package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.Payment
import com.helloyatri.databinding.AccountPaymentRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.textdecorator.TextDecorator

class AdapterAccountPayment :
    AdvanceRecycleViewAdapter<AdapterAccountPayment.ViewHolder, Payment>() {

    inner class ViewHolder(private val binding: AccountPaymentRowItemBinding) :
        BaseHolder<Payment>(binding.root) {

        override fun bind(item: Payment) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.user?.profileImage)
            textViewUserName.text = item.user?.name.nullify()
//            textViewWasMinutesAway.text = item.awayDuration
            textViewPaymentType.text = context.getString(R.string.dummy_cash_payment)
            textViewStartLocation.text = item.startAddress.nullify()
            textViewDestinationLocation.text = item.endAddress.nullify()

            TextDecorator.decorate(
                textViewDistance, String.format(
                    getString(R.string.label_dynamic_distance_n105_5_km),
                    item.distanceTxt.nullify(Constants.DEFAULT_DISTANCE)
                )
            )
                .setTypeface(
                    R.font.lufga_medium,
                    item.distanceTxt.nullify(Constants.DEFAULT_DISTANCE)
                )
                .setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    item.distanceTxt.nullify(Constants.DEFAULT_DISTANCE)
                ).build()

            TextDecorator.decorate(
                textViewDuration, String.format(
                    getString(R.string.label_dynamic_duration_n02_40_hr),
                    item.durationTxt.nullify(Constants.DEFAULT_HOURS)
                )
            )
                .setTypeface(R.font.lufga_medium, item.durationTxt.nullify(Constants.DEFAULT_HOURS))
                .setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    item.durationTxt.nullify(Constants.DEFAULT_HOURS)
                ).build()

            TextDecorator.decorate(
                textViewFairPrice,
                String.format(
                    getString(R.string.label_dynamic_fare_price_n_780_paymenbt),
                    item.commonTotalFareTxt.nullify(Constants.DEFAULT_PRICE)
                )
            )
                .setTypeface(
                    R.font.lufga_medium,
                    item.commonTotalFareTxt.nullify(Constants.DEFAULT_PRICE)
                )
                .setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                    item.commonTotalFareTxt.nullify(Constants.DEFAULT_PRICE)
                ).build()
            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: Payment) {
        holder.bind(item)
    }
}
