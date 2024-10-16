package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.Trips
import com.helloyatri.databinding.ItemRowHomePickupsBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.toBinding

class AdapterRidesForPickups :
        AdvanceRecycleViewAdapter<AdapterRidesForPickups.ViewHolder, Trips>() {

    inner class ViewHolder(private val binding: ItemRowHomePickupsBinding) :
            BaseHolder<Trips>(binding.root) {

        override fun bind(item: Trips) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.user?.profileImage)
            textViewUserName.text = item.user?.name.nullify()
            textViewPaymentType.text = context.getString(R.string.dummy_cash_payment)
            textViewStartLocation.text = item.startAddress.nullify()
            textViewDestinationLocation.text = item.endAddress.nullify()
            textViewNavigateTo.setOnClickListener {
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

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: Trips) {
        holder.bind(item)
    }
}
