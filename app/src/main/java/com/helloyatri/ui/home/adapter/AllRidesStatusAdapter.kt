package com.helloyatri.ui.home.adapter


import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.data.model.Status
import com.helloyatri.databinding.ItemRowActiveRideBinding
import com.helloyatri.databinding.ItemRowCompletedCancelledRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.ui.base.adavancedrecyclerview.OnRecycleItemClick
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator


class AllRidesStatusAdapter :
        AdvanceRecycleViewAdapter<BaseHolder<RidePickUps>, RidePickUps>(arrayListOf()) {

    private var recyclerViewClickListener: OnRecycleItemClick<RidePickUps>? = null
    private val TYPE_1 = 1
    private val TYPE_2 = 2

    inner class ViewHolderActive(private val binding: ItemRowActiveRideBinding) :
            BaseHolder<RidePickUps>(binding.root) {

        override fun bind(item: RidePickUps) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.customerProfileImage)
            textViewUserName.text = item.customerName
            setUpTextDecoration()
            itemView.setOnClickListener {
                recyclerViewClickListener?.onClick(item, it)
            }
        }

        private fun setUpTextDecoration() = with(binding) {
            TextDecorator.decorate(textViewFairPrice, textViewFairPrice.trimmedText)
                    .setTypeface(R.font.lufga_medium, "₹780")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "₹780").build()

            TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                    .setTypeface(R.font.lufga_medium, "25.5 Km")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "25.5 Km").build()

            TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                    .setTypeface(R.font.lufga_medium, "45 min")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "45 min").build()
        }
    }

    inner class ViewHolderCompletedAndCancelled(
            private val binding: ItemRowCompletedCancelledRideBinding) :
            BaseHolder<RidePickUps>(binding.root) {
        override fun bind(item: RidePickUps) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.customerProfileImage)
            textViewUserName.text = item.customerName
            setUpTextDecoration()
            if (item.rideStatus == Status.CANCELLED) {
                textViewLabelReason.show()
                textViewDateAndTime.show()
                textViewCancelByRider.show()
                textViewReason.show()
                textViewRating.hide()
            } else {
                textViewLabelReason.hide()
                textViewDateAndTime.hide()
                textViewCancelByRider.hide()
                textViewReason.show()
                textViewRating.show()
            }
        }

        private fun setUpTextDecoration() = with(binding) {
            TextDecorator.decorate(textViewFairPrice, textViewFairPrice.trimmedText)
                    .setTypeface(R.font.lufga_medium, "₹780")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "₹780").build()

            TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                    .setTypeface(R.font.lufga_medium, "25.5 Km")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "25.5 Km").build()

            TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                    .setTypeface(R.font.lufga_medium, "45 min")
                    .setAbsoluteSize(context.resources.getDimensionPixelSize(R.dimen._14ssp),
                            "45 min").build()
        }
    }

    override fun getViewType(position: Int): Int {
        return if (items?.get(position)?.rideStatus == Status.ACTIVE) TYPE_1
        else TYPE_2

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): BaseHolder<RidePickUps> {
        return when (viewType) {
            TYPE_1 -> ViewHolderActive(parent.toBinding())
            TYPE_2 -> ViewHolderCompletedAndCancelled(parent.toBinding())
            else -> ViewHolderActive(parent.toBinding())
        }
    }

    override fun onBindDataHolder(
            holder: BaseHolder<RidePickUps>,
            position: Int,
            item: RidePickUps,
    ) {
        when (holder) {
            is ViewHolderActive -> {
                holder.bind(item)
            }

            is ViewHolderCompletedAndCancelled -> {
                holder.bind(item)
            }
        }
    }
}
