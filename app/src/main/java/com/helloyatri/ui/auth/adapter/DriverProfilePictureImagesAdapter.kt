package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.DriverProfilePictureImages
import com.helloyatri.databinding.AuthDriverPersonalProfilePictureImageRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.toBinding

class DriverProfilePictureImagesAdapter :
        AdvanceRecycleViewAdapter<DriverProfilePictureImagesAdapter.ViewHolder, DriverProfilePictureImages>() {

    var isBitMap = false

    inner class ViewHolder(
            private val binding: AuthDriverPersonalProfilePictureImageRowItemBinding) :
            BaseHolder<DriverProfilePictureImages>(binding.root) {

        override fun bind(item: DriverProfilePictureImages) = with(binding) {
            if (isBitMap) {
                imageViewProfile.setImageBitmap(item.imageBitmap)
            } else {
                imageViewProfile.loadImageFromServerWithPlaceHolder(item.images)
            }

            imageViewCancel.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int,
                                  item: DriverProfilePictureImages
    ) {
        holder.bind(item)
    }
}