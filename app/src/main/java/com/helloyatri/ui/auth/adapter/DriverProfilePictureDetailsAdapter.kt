package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import com.helloyatri.data.request.DriverProfilePictureDetails
import com.helloyatri.databinding.AuthDriverProfilePictureDetailsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class DriverProfilePictureDetailsAdapter :
        AdvanceRecycleViewAdapter<DriverProfilePictureDetailsAdapter.ViewHolder, DriverProfilePictureDetails>() {

    inner class ViewHolder(private val binding: AuthDriverProfilePictureDetailsRowItemBinding) :
            BaseHolder<DriverProfilePictureDetails>(binding.root) {

        override fun bind(item: DriverProfilePictureDetails) = with(binding) {
            textViewDetails.text = item.text
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int,
                                  item: DriverProfilePictureDetails) {
        holder.bind(item)
    }
}