package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.AccountPaymentData
import com.helloyatri.databinding.AccountPaymentRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator

class AdapterAccountPayment :
        AdvanceRecycleViewAdapter<AdapterAccountPayment.ViewHolder, AccountPaymentData>() {

    inner class ViewHolder(private val binding: AccountPaymentRowItemBinding) :
            BaseHolder<AccountPaymentData>(binding.root) {

        override fun bind(item: AccountPaymentData) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.userImage)
            textViewUserName.text = item.userName
            textViewWasMinutesAway.text = item.awayDuration
            textViewPaymentType.text = item.paymentType
            textViewStartLocation.text = item.startLocation
            textViewDestinationLocation.text = item.endLocation
            textViewDistance.text = String.format("Distance \n%s", item.distance)
            textViewDuration.text = String.format("Duration \n%s", item.duration)
            textViewFairPrice.text = String.format("Fare Price \n%s", item.farePrice)
            setUpTextDecoration(item)

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }

        private fun setUpTextDecoration(item: AccountPaymentData) = with(binding) {
            TextDecorator.decorate(textViewFairPrice, textViewFairPrice.trimmedText)
                    .setTypeface(R.font.lufga_medium, item.farePrice.toString())
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            item.farePrice.toString()).build()

            TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                    .setTypeface(R.font.lufga_medium, item.distance.toString())
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            item.distance.toString()).build()

            TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                    .setTypeface(R.font.lufga_medium, item.duration.toString())
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            item.duration.toString()).build()
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: AccountPaymentData) {
        holder.bind(item)
    }
}
