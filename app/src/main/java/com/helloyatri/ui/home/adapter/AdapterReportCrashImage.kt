package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.NearByLocation
import com.helloyatri.data.model.ReportCrashImageDataModel
import com.helloyatri.databinding.AuthDriverPersonalProfilePictureImageRowAddItemBinding
import com.helloyatri.databinding.RowItemNearByLocationBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding

class AdapterReportCrashImage :
        AdvanceRecycleViewAdapter<AdapterReportCrashImage.ViewHolder, ReportCrashImageDataModel>() {

    inner class ViewHolder(private val binding: AuthDriverPersonalProfilePictureImageRowAddItemBinding) :
            BaseHolder<ReportCrashImageDataModel>(binding.root) {

        override fun bind(item: ReportCrashImageDataModel) = with(binding) {
            if (adapterPosition == 0) {
                imageViewCancel.setImageResource(R.drawable.add_a_photo_24)
            } else {

                imageViewCancel.loadImageFromServerWithPlaceHolder(
                    item.image ?: ""
                )
            }

            root.setOnClickListener {
                onClickPositionListener?.invoke(item, adapterPosition)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: ReportCrashImageDataModel) {
        holder.bind(item)
    }
}
