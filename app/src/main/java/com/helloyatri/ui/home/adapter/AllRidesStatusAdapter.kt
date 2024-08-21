package com.helloyatri.ui.home.adapter


import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.data.model.Status
import com.helloyatri.data.model.Trips
import com.helloyatri.databinding.ItemRowActiveRideBinding
import com.helloyatri.databinding.ItemRowCompletedCancelledRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.ui.base.adavancedrecyclerview.OnRecycleItemClick
import com.helloyatri.utils.AppUtils.fairValue
import com.helloyatri.utils.AppUtils.fareAmount
import com.helloyatri.utils.AppUtils.openCallDialer
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator


class AllRidesStatusAdapter(
    val onEmergencyClick: (Int) -> Unit, val onEndHereClick: (Int) -> Unit
) : AdvanceRecycleViewAdapter<BaseHolder<Trips>, Trips>(arrayListOf()) {

    private var recyclerViewClickListener: OnRecycleItemClick<Trips>? = null
    private val TYPE_1 = 1
    private val TYPE_2 = 2

    inner class ViewHolderActive(private val binding: ItemRowActiveRideBinding) :
        BaseHolder<Trips>(binding.root) {

        override fun bind(item: Trips) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.user?.profileImage)
            textViewUserName.text = item.user?.name.nullify()
            textViewPaymentType.text = item.user?.name.nullify() // TODO: Set Rider Payment Type
            textViewEstimatedReachTime.text = String.format(
                getString(R.string.label_dynamic_you_have_to_reach_in_45_20min),
                item.estimatedArrivingDuration?.fairValue("0 Min")
            ) // TODO: Set Driver Reach Time
            textViewStartLocation.text = item.startAddress?.nullify("-")
            textViewDestinationLocation.text = item.endAddress?.nullify("-")
            item.estimatedFare?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewFairPrice, String.format(
                        getString(R.string.label_dynamic_fare_price_n_780), it
                    )
                ).setTypeface(R.font.lufga_medium, getString(R.string.label_currency))
                    .setAbsoluteSize(
                        context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                        getString(R.string.label_currency)
                    ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                        context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                    ).build()
            }
            item.estimatedDistance?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewDistance, String.format(
                        getString(R.string.label_dynamic_distance_n105_5_km), it
                    )
                ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                ).build()
            }

            item.estimatedDuration?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewDuration, String.format(
                        getString(R.string.label_dynamic_duration_n02_40_hr), it
                    )
                ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                ).build()
            }
            imageViewCall.setOnClickListener { imageViewCall.context.openCallDialer(item.user?.mobile) }
            textViewEndHere.setOnClickListener {
                onEndHereClick(bindingAdapterPosition)
            }
            textViewEmergency.setOnClickListener {
                onEmergencyClick(bindingAdapterPosition)
            }
            itemView.setOnClickListener {
                recyclerViewClickListener?.onClick(item, it)
            }
        }
    }

    inner class ViewHolderCompletedAndCancelled(
        private val binding: ItemRowCompletedCancelledRideBinding
    ) : BaseHolder<Trips>(binding.root) {
        override fun bind(item: Trips) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.user?.profileImage)
            textViewUserName.text = item.user?.name.nullify()
            // TODO: Set Rider Payment Type
            textViewPaymentType.text = item.user?.name.nullify()

            item.estimatedFare?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewFairPrice, String.format(
                        getString(R.string.label_dynamic_fare_price_n_780), it
                    )
                ).setTypeface(R.font.lufga_medium, getString(R.string.label_currency))
                    .setAbsoluteSize(
                        context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                        getString(R.string.label_currency)
                    ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                ).build()
            }
            item.estimatedDistance?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewDistance, String.format(
                        getString(R.string.label_dynamic_distance_n105_5_km), it
                    )
                ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                ).build()
            }

            item.estimatedDuration?.fareAmount()?.let {
                TextDecorator.decorate(
                    textViewDuration, String.format(
                        getString(R.string.label_dynamic_duration_n02_40_hr), it
                    )
                ).setTypeface(R.font.lufga_medium, it).setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                ).build()
            }
            if (item.status == "CANCELLED") {
                textViewLabelReason.show()
                textViewDateAndTime.show()
                textViewCancelByRider.show()
                textViewRating.hide()
                textViewCancelByRider.text = String.format(getString(R.string.label_dynamic_cancel_by_rider), item.cancelledBy?.nullify())
                // TODO: Set Trip date and time
                textViewDateAndTime.text = item.cancelledBy?.nullify()
                textViewReason.text = item.cancelReason?.nullify()
            } else {
                textViewLabelReason.hide()
                textViewDateAndTime.hide()
                textViewCancelByRider.hide()
                textViewRating.show()
                textViewReason.text = item.pickupNote?.nullify()
                textViewRating.text = item.reviewRatings?.nullify("0.0")
            }
            textViewStartLocation.text = item.startAddress?.nullify("-")
            textViewDestinationLocation.text = item.endAddress?.nullify("-")
        }
    }

    override fun getViewType(position: Int): Int {
        return if (items?.get(position)?.status == "ACTIVE") TYPE_1
        else TYPE_2

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): BaseHolder<Trips> {
        return when (viewType) {
            TYPE_1 -> ViewHolderActive(parent.toBinding())
            TYPE_2 -> ViewHolderCompletedAndCancelled(parent.toBinding())
            else -> ViewHolderActive(parent.toBinding())
        }
    }

    override fun onBindDataHolder(
        holder: BaseHolder<Trips>,
        position: Int,
        item: Trips,
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
