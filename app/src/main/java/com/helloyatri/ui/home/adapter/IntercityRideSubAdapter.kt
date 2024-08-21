package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.databinding.ItemRowSubIntercityRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator

class IntercityRideSubAdapter :
        AdvanceRecycleViewAdapter<IntercityRideSubAdapter.ViewHolder, RidePickUps>() {

    private var onClickDeclineOrAccept: ((item: RidePickUps, action: String) -> Unit)? = null

    fun setOnDeclineOrAcceptClickListener(
            onClickDeclineOrAccept: (item: RidePickUps, action: String) -> Unit) {
        this.onClickDeclineOrAccept = onClickDeclineOrAccept
    }

    inner class ViewHolder(private val binding: ItemRowSubIntercityRideBinding) :
            BaseHolder<RidePickUps>(binding.root) {

        override fun bind(item: RidePickUps): Unit = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.customerProfileImage)
            textViewUserName.text = item.customerName

            if (item.isShowWaitingTime == true) {
                textViewDateTime.show()
            } else {
                textViewDateTime.hide()
            }

            setTextDecorator()
            root.setOnClickListener {
                onClickListener?.invoke(item)
            }

            textViewAccept.setOnClickListener {
                onClickDeclineOrAccept?.invoke(item, "Accept")
            }

            textViewDecline.setOnClickListener {
                onClickDeclineOrAccept?.invoke(item, "Decline")
            }
        }

        private fun setTextDecorator() = with(binding) {

            TextDecorator.decorate(textViewFairPrice, textViewFairPrice.trimmedText)
                    .setTypeface(R.font.lufga_medium, "₹780")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            "₹780").build()

            TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                    .setTypeface(R.font.lufga_medium, "25.5 Km")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            "25.5 Km").build()

            TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                    .setTypeface(R.font.lufga_medium, "45 min")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            "45 min").build()
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }
}