package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.databinding.ItemScheduleRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator

class ScheduleRideSubAdapter :
        AdvanceRecycleViewAdapter<ScheduleRideSubAdapter.ViewHolder, RidePickUps>() {

    inner class ViewHolder(private val binding: ItemScheduleRideBinding) :
            BaseHolder<RidePickUps>(binding.root) {

        override fun bind(item: RidePickUps): Unit = with(binding) {
//            val receiver = EventReceiver()

            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.customerProfileImage)
            textViewUserName.text = item.customerName

            if (item.isShowWaitingTime == true) {
                textViewReachInTime.show()
            } else {
                textViewReachInTime.hide()
            }

            TextDecorator.decorate(textViewNotes, textViewNotes.trimmedText)
                    .setTextColor(R.color.homeBgBlueColor, "Note:").build()

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
            textViewNavigateTo.setOnClickListener {
//                onClickPositionListener?.invoke(item,position)
            }

            textViewCancelRide.setOnClickListener {
            }
        }

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }
}