package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.RidePickUps
import com.helloyatri.databinding.ItemRowHomePickupsBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.toBinding

class AdapterRidesForPickups :
        AdvanceRecycleViewAdapter<AdapterRidesForPickups.ViewHolder, RidePickUps>() {

    inner class ViewHolder(private val binding: ItemRowHomePickupsBinding) :
            BaseHolder<RidePickUps>(binding.root) {

        override fun bind(item: RidePickUps) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.customerProfileImage)
            textViewUserName.text = item.customerName
            textViewPaymentType.text = item.paymentType

            textViewNavigateTo.setOnClickListener {
                onClickViewListener?.invoke(item, it)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: RidePickUps) {
        holder.bind(item)
    }
}
