package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.Trips
import com.helloyatri.databinding.ItemScheduleRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.textdecorator.TextDecorator

class ScheduleRideSubAdapter :
        AdvanceRecycleViewAdapter<ScheduleRideSubAdapter.ViewHolder, Trips>() {

    inner class ViewHolder(private val binding: ItemScheduleRideBinding) :
            BaseHolder<Trips>(binding.root) {

        override fun bind(item: Trips): Unit = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.user?.profileImage)
            textViewUserName.text = item.user?.name.nullify()
            textViewReachInTime.hide()
            textViewStartLocation.text = item.startAddress.nullify()
            textViewDestinationLocation.text = item.endAddress.nullify()
            textViewFareTotal.text = item.endAddress.nullify()
            item.duration_txt?.let {
                textViewReachInTime.show()
                textViewReachInTime.text = String.format(getString(R.string.label_dynamic_you_have_to_reach_in_45_20min),item.duration_txt.nullify())
            } ?: {
                textViewReachInTime.hide()
            }
            TextDecorator.decorate(textViewNotes, String.format(getString(R.string.label_dynamic_note_note_message_show_here),item.wheelchairNote.nullify()))
                    .setTextColor(R.color.homeBgBlueColor, "Note:")
                    .build()
            TextDecorator.decorate(textViewFareTotal, String.format(getString(R.string.label_fare_total),item.commonTotalFareTxt.nullify()))
                .setAbsoluteSize(
                    context.resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._18ssp),
                    item.commonTotalFareTxt.nullify()
                )
                .build()
            textViewNavigateTo.setOnClickListener {
               onClickViewListener?.invoke(item, it)
            }
            imageViewCall.setOnClickListener {
               onClickViewListener?.invoke(item, it)
            }
            textViewCancelRide.setOnClickListener {
                onClickViewListener?.invoke(item, it)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }
}